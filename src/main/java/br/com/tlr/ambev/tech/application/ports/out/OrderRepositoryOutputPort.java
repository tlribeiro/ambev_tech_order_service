package br.com.tlr.ambev.tech.application.ports.out;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

public interface OrderRepositoryOutputPort {

    Mono<Page<Order>> findAllPage(int page, int size);

    Mono<Order> findById(String id);

    Mono<Order> findByRefId(String id);

    Mono<Order> save(Order order);

}
