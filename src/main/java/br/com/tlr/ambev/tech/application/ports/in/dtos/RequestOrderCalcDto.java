package br.com.tlr.ambev.tech.application.ports.in.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestOrderCalcDto {
    @NotNull(message = "Código de Referência é obrigatório")
    private String refId;

    @NotNull(message = "Cliente é obrigatório")
    private String client;

    @NotNull(message = "Lista de itens é obrigatória")
    @NotEmpty(message = "Lista de itens não informada")
    private List<RequestOrderItemDto> items;
}
