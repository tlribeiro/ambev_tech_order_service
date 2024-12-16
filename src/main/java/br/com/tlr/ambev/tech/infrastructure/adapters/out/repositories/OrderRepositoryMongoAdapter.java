package br.com.tlr.ambev.tech.infrastructure.adapters.out.repositories;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.ports.out.OrderRepositoryOutputPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OrderRepositoryMongoAdapter implements OrderRepositoryOutputPort {

    private final PedidoRepository pedidoRepository;

    public OrderRepositoryMongoAdapter(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Mono<Page<Order>> findAllPage(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return this.pedidoRepository.findAllBy(pageable)
                .collectList()
                .zipWith(this.pedidoRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Order> findById(String id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Mono<Order> findByRefId(String refId) {
        return pedidoRepository.findByRefId(refId);
    }

    @Override
    public Mono<Order> save(Order order) {
        return pedidoRepository.save(order);
    }
}

@Repository
interface PedidoRepository extends ReactiveMongoRepository<Order, String> {

    Mono<Order> findByRefId(String cliente);

    Flux<Order> findAllBy(Pageable pageable);
}
