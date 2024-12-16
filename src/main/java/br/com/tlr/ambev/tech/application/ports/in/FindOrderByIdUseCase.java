package br.com.tlr.ambev.tech.application.ports.in;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import reactor.core.publisher.Mono;

public interface FindOrderByIdUseCase {
    Mono<Order> findById(String id);
}
