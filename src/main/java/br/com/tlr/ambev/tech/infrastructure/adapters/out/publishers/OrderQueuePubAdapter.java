package br.com.tlr.ambev.tech.infrastructure.adapters.out.publishers;

import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import br.com.tlr.ambev.tech.application.ports.out.OrderQueueOutputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OrderQueuePubAdapter extends AbstractPubSubPublisherAdapter implements OrderQueueOutputPort {

    private final String topicName;

    public OrderQueuePubAdapter(@Value("${pubsub.topic.orders-queue}") String topicName,
                                TopicAdminClient topicAdminClient,
                                ObjectMapper mapper) {

        super(topicAdminClient, mapper);
        this.topicName = topicName;
    }

    @Override
    public Mono<Void> sentToQueue(RequestOrderCalcDto requestOrderCalcDto) {
        return this.publishMessage(requestOrderCalcDto);
    }

    @Override
    protected String getTopicName() {
        return topicName;
    }
}
