package br.com.tlr.ambev.tech.infrastructure.adapters.in.controllers;

import br.com.tlr.ambev.tech.application.ports.in.ReceiveAsyncOrderValueUseCase;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderItemDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@WebFluxTest(OrderProducerControllerAdapter.class)
public class OrderProducerControllerAdapterTest {

    public static final String BASE_PATH = "/api/producers/orders";
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReceiveAsyncOrderValueUseCase receiveAsyncOrder;

    @Test
    void shouldReturnBadRequestIfRefIdIsNull() {
        RequestOrderCalcDto invalidRequest = new RequestOrderCalcDto();
        invalidRequest.setRefId(null);

        Mockito.when(receiveAsyncOrder.sendToQueue(ArgumentMatchers.any(RequestOrderCalcDto.class)))
                .thenReturn(Mono.error(new ConstraintViolationException("Código de Referência é obrigatório", Collections.emptySet())));

        webTestClient.post()
                .uri(BASE_PATH)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnAcceptedIfRequestIsValid() {
        RequestOrderCalcDto validRequest = new RequestOrderCalcDto();
        validRequest.setRefId("123");
        validRequest.setClient("Client A");

        RequestOrderItemDto item = new RequestOrderItemDto();
        item.setName("Produto 1");
        validRequest.setItems(List.of(item));

        Mockito.when(receiveAsyncOrder.sendToQueue(ArgumentMatchers.any(RequestOrderCalcDto.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri(BASE_PATH)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
    }
}