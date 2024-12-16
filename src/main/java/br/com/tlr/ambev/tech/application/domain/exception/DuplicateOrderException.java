package br.com.tlr.ambev.tech.application.domain.exception;

public class DuplicateOrderException extends RuntimeException {
    public DuplicateOrderException() {
        super("Pedido duplicado!");
    }
}
