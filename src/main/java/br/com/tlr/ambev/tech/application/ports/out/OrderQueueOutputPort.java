package br.com.tlr.ambev.tech.application.ports.out;

import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import reactor.core.publisher.Mono;

public interface OrderQueueOutputPort {
    Mono<Void> sentToQueue(RequestOrderCalcDto requestOrderCalcDto);
}
