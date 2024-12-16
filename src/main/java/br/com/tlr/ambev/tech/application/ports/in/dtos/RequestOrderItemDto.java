package br.com.tlr.ambev.tech.application.ports.in.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RequestOrderItemDto {
    @NotNull(message = "Código de Referência é obrigatório")
    private String refId;
    @NotNull(message = "Nome do produto é obrigatório")
    private String name;
    @NotNull(message = "Preço do produto é obrigatório")
    private BigDecimal price;
    @NotNull(message = "Preço do produto é obrigatório")
    private Integer quantity;
}
