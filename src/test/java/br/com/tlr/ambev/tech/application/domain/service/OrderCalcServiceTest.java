package br.com.tlr.ambev.tech.application.domain.service;

import br.com.tlr.ambev.tech.application.domain.exception.DuplicateOrderException;
import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderItemDto;
import br.com.tlr.ambev.tech.application.ports.out.OrderListenerOutputPort;
import br.com.tlr.ambev.tech.application.ports.out.OrderRepositoryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderCalcServiceTest {

    public static final String CLIENT_NAME = "Test Client";
    public static final String PRODUCT_REF_1 = "ref 1";
    public static final String PRODUCT_REF_2 = "ref 2";
    public static final String PRODUTO_NAME_1 = "Produto 1";
    public static final String PRODUTO_NAME_2 = "Produto 2";
    public static final String ORDER_REF_ID = "123";
    @Mock
    private OrderRepositoryOutputPort pedidoRepository;

    @Mock
    private OrderListenerOutputPort orderListenerOutputPort;

    @InjectMocks
    private OrderCalcService orderCalcService;

    @Test
    void shouldThrowExceptionWhenOrderAlreadyExists() {
        String refId = ORDER_REF_ID;
        RequestOrderCalcDto request = new RequestOrderCalcDto();
        request.setRefId(refId);

        Order existingOrder = new Order();
        existingOrder.setRefId(refId);

        Mockito.when(pedidoRepository.findByRefId(refId)).thenReturn(Mono.just(existingOrder));

        StepVerifier.create(orderCalcService.calculate(request))
                .expectError(DuplicateOrderException.class)
                .verify();

        Mockito.verify(pedidoRepository, Mockito.times(1)).findByRefId(refId);
        Mockito.verifyNoInteractions(orderListenerOutputPort);
    }

    public static RequestOrderItemDto createRequestOrderItemDto(String refId, String name, BigDecimal price, Integer quantity) {
        RequestOrderItemDto dto = new RequestOrderItemDto();

        dto.setRefId(refId);
        dto.setName(name);
        dto.setPrice(price);
        dto.setQuantity(quantity);

        return dto;
    }

    @Test
    void shouldSaveOrderAndNotifySuccessfully() {
        RequestOrderCalcDto request = new RequestOrderCalcDto();
        request.setRefId(ORDER_REF_ID);
        request.setClient(CLIENT_NAME);
        request.setItems(List.of(
                createRequestOrderItemDto(PRODUCT_REF_1, PRODUTO_NAME_1, BigDecimal.valueOf(10), 2),
                createRequestOrderItemDto(PRODUCT_REF_2, PRODUTO_NAME_2, BigDecimal.valueOf(10), 1)
        ));

        Order savedOrder = new Order();
        savedOrder.setRefId(ORDER_REF_ID);
        savedOrder.setClientName(CLIENT_NAME);
        savedOrder.setTotalValue(BigDecimal.valueOf(30));

        Mockito.when(pedidoRepository.findByRefId(ORDER_REF_ID)).thenReturn(Mono.empty());
        Mockito.when(pedidoRepository.save(Mockito.any(Order.class))).thenReturn(Mono.just(savedOrder));
        Mockito.when(orderListenerOutputPort.notify(Mockito.any(Order.class))).thenReturn(Mono.empty());

        StepVerifier.create(orderCalcService.calculate(request))
                .expectNext(savedOrder)
                .verifyComplete();

        Mockito.verify(pedidoRepository, Mockito.times(1)).findByRefId(ORDER_REF_ID);
        Mockito.verify(pedidoRepository, Mockito.times(1)).save(Mockito.any(Order.class));
        Mockito.verify(orderListenerOutputPort, Mockito.times(1)).notify(savedOrder);
    }

    @Test
    void shouldSaveOrderButFailToNotify() {
        String refId = ORDER_REF_ID;

        RequestOrderCalcDto request = new RequestOrderCalcDto();
        request.setRefId(refId);
        request.setClient(CLIENT_NAME);
        request.setItems(List.of(
                createRequestOrderItemDto(PRODUCT_REF_1, PRODUTO_NAME_1, BigDecimal.valueOf(10), 2),
                createRequestOrderItemDto(PRODUCT_REF_2, PRODUTO_NAME_2, BigDecimal.valueOf(10), 1)
        ));

        Order savedOrder = new Order();
        savedOrder.setRefId(refId);
        savedOrder.setClientName(CLIENT_NAME);
        savedOrder.setTotalValue(BigDecimal.valueOf(30));

        Mockito.when(pedidoRepository.findByRefId(refId)).thenReturn(Mono.empty());
        Mockito.when(pedidoRepository.save(Mockito.any(Order.class))).thenReturn(Mono.just(savedOrder));
        Mockito.when(orderListenerOutputPort.notify(Mockito.any(Order.class)))
                .thenReturn(Mono.error(new RuntimeException("Notification failed")));

        StepVerifier.create(orderCalcService.calculate(request))
                .expectError(RuntimeException.class)
                .verify();

        Mockito.verify(pedidoRepository, Mockito.times(1)).findByRefId(refId);
        Mockito.verify(pedidoRepository, Mockito.times(1)).save(Mockito.any(Order.class));
        Mockito.verify(orderListenerOutputPort, Mockito.times(1)).notify(savedOrder);
    }
}