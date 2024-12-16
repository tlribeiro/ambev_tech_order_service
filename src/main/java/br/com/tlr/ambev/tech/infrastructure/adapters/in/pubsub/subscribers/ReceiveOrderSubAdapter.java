package br.com.tlr.ambev.tech.infrastructure.adapters.in.pubsub.subscribers;

import br.com.tlr.ambev.tech.application.domain.exception.DuplicateOrderException;
import br.com.tlr.ambev.tech.application.ports.in.CalculateOrderValueUseCase;
import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiveOrderSubAdapter {

    private final CalculateOrderValueUseCase useCase;

    @Profile("!test")
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    void consumer(List<RequestOrderCalcDto> payload,
                  @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {

        for (RequestOrderCalcDto requestOrderCalcDto : payload) {
            try {
                useCase.calculate(requestOrderCalcDto).block();

                log.info("Pedido salvo com sucesso!");

                message.ack();
            } catch (RuntimeException e) {
                if (isIgnorableException(e)) {

                    //Aqui poderia ser gerado um evento para outro processo para notificar/registrar o erro, por exemplo.

                    log.info("Registro Ignorado - Motivo: {}", e.getMessage());

                    message.ack();
                } else {
                    log.error("Pedido - Erro", e);
                    message.nack();
                }
            }
        }
    }

    private boolean isIgnorableException(RuntimeException e) {
        return e instanceof DuplicateOrderException || e instanceof ConstraintViolationException;
    }
}
