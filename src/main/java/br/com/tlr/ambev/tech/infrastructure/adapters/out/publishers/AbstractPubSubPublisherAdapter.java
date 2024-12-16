package br.com.tlr.ambev.tech.infrastructure.adapters.out.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import reactor.core.publisher.Mono;

import java.util.Collections;

public abstract class AbstractPubSubPublisherAdapter {

    private final ObjectMapper mapper;
    private final TopicAdminClient topicAdminClient;

    public AbstractPubSubPublisherAdapter(TopicAdminClient topicAdminClient, ObjectMapper mapper) {
        this.topicAdminClient = topicAdminClient;
        this.mapper = mapper;
    }

    protected ObjectMapper getMapper() {
        return mapper;
    }

    protected abstract String getTopicName();

    public Mono<Void> publishMessage(Object entity) {
        return Mono.create(sink -> {
            try {

                String message = convertToString(entity);

                PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                        .setData(ByteString.copyFromUtf8(message))
                        .build();

                topicAdminClient.publish(getTopicName(), Collections.singletonList(pubsubMessage));

                sink.success();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }

    protected String convertToString(Object object) {
        String message;
        try {
            message = this.getMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}