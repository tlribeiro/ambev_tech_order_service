package br.com.tlr.ambev.tech.infrastructure.config;

import br.com.tlr.ambev.tech.application.ports.in.dtos.RequestOrderCalcDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ChannelConfiguration {

    private final String topicSubscriptionId;

    public ChannelConfiguration(@Value("${pubsub.subscription.orders-queue}") String topicSubscriptionId) {
        this.topicSubscriptionId = topicSubscriptionId;
    }

    @Bean(name = "pubsubInputChannel")
    public MessageChannel inputMessageChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    @Primary
    public PubSubMessageConverter pubSubMessageConverter(ObjectMapper objectMapper) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new JacksonPubSubMessageConverter(objectMapper);
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
        var adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, topicSubscriptionId);
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setOutputChannel(inputChannel);
        adapter.setPayloadType(RequestOrderCalcDto.class);
        return adapter;
    }
}

