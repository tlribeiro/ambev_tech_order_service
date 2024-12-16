package br.com.tlr.ambev.tech.application.domain.service;

import br.com.tlr.ambev.tech.application.domain.exception.DuplicateOrderException;
import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.domain.model.OrderItem;
import br.com.tlr.ambev.tech.application.domain.model.OrderStatus;
import br.com.tlr.ambev.tech.application.domain.model.Product;
import br.com.tlr.ambev.tech.application.ports.in.CalculateOrderValueUseCase;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderItemDto;
import br.com.tlr.ambev.tech.application.ports.out.OrderListenerOutputPort;
import br.com.tlr.ambev.tech.application.ports.out.OrderRepositoryOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
@Slf4j
public class OrderCalcService implements CalculateOrderValueUseCase {

    private final OrderRepositoryOutputPort pedidoRepository;

    private final OrderListenerOutputPort orderListenerOutputPort;

    public OrderCalcService(OrderRepositoryOutputPort pedidoRepository, OrderListenerOutputPort orderListenerOutputPort) {
        this.pedidoRepository = pedidoRepository;
        this.orderListenerOutputPort = orderListenerOutputPort;
    }

    @Override
    public Mono<Order> calculate(RequestOrderCalcDto requestOrderCalcDto) {
        return pedidoRepository.findByRefId(requestOrderCalcDto.getRefId())
                .flatMap(existingOrder -> {
                    return Mono.<Order>error(new DuplicateOrderException());
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            Order order = createAndCalcTotalOrder(requestOrderCalcDto);
                            return pedidoRepository.save(order)
                                    .flatMap(savedOrder ->
                                            // Envia a notificação
                                            orderListenerOutputPort.notify(savedOrder)
                                                    .thenReturn(savedOrder)
                                    );
                        })
                );
    }

    private static Order createAndCalcTotalOrder(RequestOrderCalcDto requestOrderCalcDto) {
        Order order = new Order();

        order.setRefId(requestOrderCalcDto.getRefId());
        order.setClientName(requestOrderCalcDto.getClient());
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();

        BigDecimal totalValue = requestOrderCalcDto.getItems().stream()
                .map(item -> {

                    OrderItem orderItem = createAndCalcTotalProduct(item);

                    items.add(orderItem);

                    return orderItem.getTotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setStatus(OrderStatus.CALCULATED);
        order.setItems(items);
        order.setTotalValue(totalValue);

        return order;
    }

    private static OrderItem createAndCalcTotalProduct(RequestOrderItemDto item) {
        Product product = new Product();

        product.setRefId(item.getRefId());
        product.setName(item.getName());
        product.setPrice(item.getPrice());

        OrderItem orderItem = new OrderItem();

        orderItem.setProduct(product);

        orderItem.setQuantity(item.getQuantity());
        orderItem.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        return orderItem;
    }
}
