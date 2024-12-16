package br.com.tlr.ambev.tech.application.ports.in;

import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

public interface ReceiveAsyncOrderValueUseCase {
    Mono<Void> sendToQueue(@NotNull @Valid RequestOrderCalcDto requestOrderCalcDto);
}
