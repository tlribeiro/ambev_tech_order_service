package br.com.tlr.ambev.tech.application.ports.in;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

public interface ListOrdersUseCase {

    Mono<Page<Order>> list(int page, int size);
}
