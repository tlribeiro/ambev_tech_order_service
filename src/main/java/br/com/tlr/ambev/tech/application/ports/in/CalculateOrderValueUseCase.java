package br.com.tlr.ambev.tech.application.ports.in;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

public interface CalculateOrderValueUseCase {

    Mono<Order> calculate(@NotNull @Valid RequestOrderCalcDto requestOrderCalcDto);
}
