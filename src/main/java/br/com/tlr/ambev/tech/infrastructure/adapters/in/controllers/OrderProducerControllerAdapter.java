package br.com.tlr.ambev.tech.infrastructure.adapters.in.controllers;

import br.com.tlr.ambev.tech.application.ports.in.ReceiveAsyncOrderValueUseCase;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * API para Receber pedidos de Forma Assincrona.
 * <p>
 * Recebe um Pedido e coloca em uma fila para processamento posterior.
 * Retorna somente o status 202 para indicar que a requisição foi aceita
 * e será processada assim que possível.
 *
 * @author Thiago Ribeiro - tlribeiro@outlook.com
 */
@RestController
@RequestMapping("/api/producers/orders")
public class OrderProducerControllerAdapter {

    private final ReceiveAsyncOrderValueUseCase receiveAsyncOrder;

    public OrderProducerControllerAdapter(ReceiveAsyncOrderValueUseCase receiveAsyncOrder) {
        this.receiveAsyncOrder = receiveAsyncOrder;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> save(@RequestBody RequestOrderCalcDto requestOrderCalcDto) {
        return receiveAsyncOrder
                .sendToQueue(requestOrderCalcDto)
                .then(Mono.just(ResponseEntity.accepted().build()));
    }
}
