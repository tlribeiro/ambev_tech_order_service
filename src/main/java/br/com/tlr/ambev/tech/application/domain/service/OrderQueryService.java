package br.com.tlr.ambev.tech.application.domain.service;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.ports.in.FindOrderByIdUseCase;
import br.com.tlr.ambev.tech.application.ports.in.ListOrdersUseCase;
import br.com.tlr.ambev.tech.application.ports.out.OrderRepositoryOutputPort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderQueryService implements FindOrderByIdUseCase, ListOrdersUseCase {

    private final OrderRepositoryOutputPort orderRepository;

    public OrderQueryService(OrderRepositoryOutputPort pedidoRepository) {
        this.orderRepository = pedidoRepository;
    }

    @Override
    public Mono<Page<Order>> list(int page, int size) {
        return orderRepository.findAllPage(page, size);
    }

    @Override
    public Mono<Order> findById(String id) {
        return orderRepository.findById(id);
    }
}
