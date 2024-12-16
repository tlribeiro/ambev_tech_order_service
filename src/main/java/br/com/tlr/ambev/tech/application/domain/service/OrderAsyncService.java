package br.com.tlr.ambev.tech.application.domain.service;

import br.com.tlr.ambev.tech.application.ports.in.ReceiveAsyncOrderValueUseCase;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import br.com.tlr.ambev.tech.application.ports.out.OrderQueueOutputPort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Service
@Validated
public class OrderAsyncService implements ReceiveAsyncOrderValueUseCase {

    private final OrderQueueOutputPort orderQueueOutputPort;

    public OrderAsyncService(OrderQueueOutputPort orderQueueOutputPort) {
        this.orderQueueOutputPort = orderQueueOutputPort;
    }

    @Override
    public Mono<Void> sendToQueue(RequestOrderCalcDto requestOrderCalcDto) {
        return orderQueueOutputPort.sentToQueue(requestOrderCalcDto);
    }
}
