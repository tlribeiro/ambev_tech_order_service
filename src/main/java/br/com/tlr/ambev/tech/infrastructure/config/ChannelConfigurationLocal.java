package br.com.tlr.ambev.tech.infrastructure.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Profile("local")
@Slf4j
@Configuration
public class ChannelConfigurationLocal {

    @Value("${spring.cloud.gcp.pubsub.project-id}")
    String projectId;

    @Value("${pubsub.topic.calculated-orders}")
    String topicCalculatedOrderName;

    @Value("${pubsub.topic.orders-queue}")
    String topicOrderQueueName;

    @Value("${pubsub.subscription.orders-queue}")
    String subscriptionOrderQueueName;

    @Bean
    public CredentialsProvider credentialsProvider() {
        return NoCredentialsProvider.create();
    }

    @Bean
    Topic topic(TopicAdminClient topicAdminClient) {
        configurarTopico(topicAdminClient, topicCalculatedOrderName);
        return configurarTopico(topicAdminClient, topicOrderQueueName);
    }

    @Bean
    Subscription subscription(PubSubAdmin pubSubAdmin) {

        Subscription subscription = null;

        try {
            subscription = pubSubAdmin.getSubscription(subscriptionOrderQueueName);

            if (subscription == null) {
                log.info("Criando o subscription local: {}", subscription);
                pubSubAdmin.createSubscription(subscriptionOrderQueueName, topicOrderQueueName);
            }
        } catch (Exception e) {
            log.error("Erro ao criar a subscription", e);
        }

        return subscription;
    }

    @Bean
    public TransportChannelProvider channelProvider() {
        return FixedTransportChannelProvider.create(GrpcTransportChannel.create(
                ManagedChannelBuilder.forTarget("localhost:8086").usePlaintext().build()
        ));
    }

    @Bean
    public SubscriberStubSettings subscriberStubSettings(
            CredentialsProvider credentialsProvider, TransportChannelProvider channelProvider) throws IOException {
        return SubscriberStubSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build();
    }


    private Topic configurarTopico(TopicAdminClient topicAdminClient, String topicName) {
        try {
            return topicAdminClient.getTopic(topicName);
        } catch (Exception e) {
            log.info("Criando o topico local: {}", topicName);
            return topicAdminClient.createTopic(topicName);
        }
    }
}
