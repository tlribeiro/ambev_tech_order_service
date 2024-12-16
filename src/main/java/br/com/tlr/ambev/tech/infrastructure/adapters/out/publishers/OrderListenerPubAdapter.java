package br.com.tlr.ambev.tech.infrastructure.adapters.out.publishers;

import br.com.tlr.ambev.tech.application.domain.model.Order;
import br.com.tlr.ambev.tech.application.ports.out.OrderListenerOutputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class OrderListenerPubAdapter extends AbstractPubSubPublisherAdapter implements OrderListenerOutputPort {

    private final String topicName;

    public OrderListenerPubAdapter(@Value("${pubsub.topic.calculated-orders}") String topicName,
                                   TopicAdminClient topicAdminClient,
                                   ObjectMapper mapper) {

        super(topicAdminClient, mapper);
        this.topicName = topicName;
    }

    @Override
    public Mono<Void> notify(Order order) {

        log.info("Enviando notificação para serviços externos - Order Id:{} - RefId: {}", order.getId(), order.getRefId());

        return this.publishMessage(order);
    }

    @Override
    protected String getTopicName() {
        return topicName;
    }
}
