package br.com.tlr.ambev.tech.application.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Product {
    private String refId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
}
