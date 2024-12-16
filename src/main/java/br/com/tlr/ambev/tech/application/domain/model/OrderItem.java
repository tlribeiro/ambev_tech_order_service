package br.com.tlr.ambev.tech.application.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItem {
    private Product product;
    private Integer quantity;
    private BigDecimal total;
}
