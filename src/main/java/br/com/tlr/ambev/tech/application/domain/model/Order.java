package br.com.tlr.ambev.tech.application.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Getter
@Setter
public class Order {
    @Id
    private String id;

    private String refId;
    private LocalDateTime createdAt;

    private String clientName;
    private OrderStatus status;
    private BigDecimal totalValue;

    private List<OrderItem> items;
}