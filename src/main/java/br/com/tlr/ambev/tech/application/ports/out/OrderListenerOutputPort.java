package br.com.tlr.ambev.tech.application.ports.out;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import reactor.core.publisher.Mono;

public interface OrderListenerOutputPort {
    Mono<Void> notify(Order order);
}