package br.com.tlr.ambev.tech.infrastructure.adapters.in.controllers;

import br.com.tlr.ambev.tech.application.domain.exception.DuplicateOrderException;
import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.ports.in.CalculateOrderValueUseCase;
import br.com.tlr.ambev.tech.application.ports.in.FindOrderByIdUseCase;
import br.com.tlr.ambev.tech.application.ports.in.ListOrdersUseCase;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import br.com.tlr.ambev.tech.infrastructure.config.RestExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@WebFluxTest(OrderControllerAdapter.class)
@Import(RestExceptionHandler.class)
class OrderControllerAdapterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FindOrderByIdUseCase findOrderByIdUseCase;

    @MockBean
    private ListOrdersUseCase listOrdersUseCase;

    @MockBean
    private CalculateOrderValueUseCase calculateOrderValueUseCase;

    @Test
    void testSaveOrder_ReturnsCreatedStatusAndLocationHeader() {
        // Arrange
        Order order = new Order();
        order.setId("123");
        order.setRefId("REF123");
        order.setClientName("João");

        Mockito.when(calculateOrderValueUseCase.calculate(ArgumentMatchers.any(RequestOrderCalcDto.class)))
                .thenReturn(Mono.just(order));

        // Act & Assert
        webTestClient.post()
                .uri("/api/orders")
                .bodyValue(order)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/orders/123");
    }

    @Test
    void testSaveOrder_ReturnsBadRequestForValidationErrors() {
        RequestOrderCalcDto requestOrderCalcDto = new RequestOrderCalcDto(); // Sem os campos obrigatórios, refId e cliente

        // Simula a exceção de ConstraintViolationException, ou validação
        Mockito.when(calculateOrderValueUseCase.calculate(ArgumentMatchers.any(RequestOrderCalcDto.class)))
                .thenReturn(Mono.error(new ConstraintViolationException("Id de Referência é obrigatório", Collections.emptySet())));

        // Act & Assert
        webTestClient.post()
                .uri("/api/orders")
                .bodyValue(requestOrderCalcDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testSaveOrder_ReturnsUnprocessableEntityForDuplicateOrder() {
        // Arrange
        RequestOrderCalcDto requestOrderCalcDto = new RequestOrderCalcDto();

        requestOrderCalcDto.setRefId("REF123");
        requestOrderCalcDto.setClient("João");
        requestOrderCalcDto.setItems(List.of());



        Mockito.when(calculateOrderValueUseCase.calculate(ArgumentMatchers.any(RequestOrderCalcDto.class)))
                .thenReturn(Mono.error(new DuplicateOrderException()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/orders")
                .bodyValue(requestOrderCalcDto)
                .exchange()
                .expectStatus().isEqualTo(422) // HTTP 422 Unprocessable Entity
                .expectBody()
                .jsonPath("$.error").isEqualTo("Ocorreu um erro de negócio")
                .jsonPath("$.details").isEqualTo("Pedido duplicado!");
    }

    @Test
    void testFindById_ReturnsOrderWhenFound() {
        // Arrange
        Order order = new Order();
        order.setId("123");
        order.setRefId("REF123");
        order.setClientName("João");

        Mockito.when(findOrderByIdUseCase.findById("123"))
                .thenReturn(Mono.just(order));

        // Act & Assert
        webTestClient.get()
                .uri("/api/orders/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123")
                .jsonPath("$.clientName").isEqualTo("João");
    }

    @Test
    void testFindById_ReturnsNotFoundWhenOrderDoesNotExist() {
        // Arrange
        Mockito.when(findOrderByIdUseCase.findById("123"))
                .thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.get()
                .uri("/api/orders/123")
                .exchange()
                .expectStatus().isNotFound();
    }
}