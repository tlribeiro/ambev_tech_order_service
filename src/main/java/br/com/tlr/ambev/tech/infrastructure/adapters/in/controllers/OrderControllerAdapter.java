package br.com.tlr.ambev.tech.infrastructure.adapters.in.controllers;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.ports.in.CalculateOrderValueUseCase;
import br.com.tlr.ambev.tech.application.ports.in.FindOrderByIdUseCase;
import br.com.tlr.ambev.tech.application.ports.in.ListOrdersUseCase;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderControllerAdapter {

    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final CalculateOrderValueUseCase calculateOrderValueUseCase;

    public OrderControllerAdapter(FindOrderByIdUseCase findOrderByIdUseCase, ListOrdersUseCase listOrdersUseCase, CalculateOrderValueUseCase calculateOrderValueUseCase) {
        this.findOrderByIdUseCase = findOrderByIdUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.calculateOrderValueUseCase = calculateOrderValueUseCase;
    }

    @GetMapping
    public Mono<Page<Order>> listAlls(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {

        return this.listOrdersUseCase.list(page, size);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> byId(@PathVariable String id) {
        return findOrderByIdUseCase.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@RequestBody RequestOrderCalcDto requestOrderCalcDto) {
        return calculateOrderValueUseCase.calculate(requestOrderCalcDto)
                .map(savedOrder -> ResponseEntity.created(
                        URI.create("/api/orders/" + savedOrder.getId())
                ).build());
    }
}
