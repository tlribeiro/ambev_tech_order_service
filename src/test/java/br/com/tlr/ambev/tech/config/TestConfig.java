package br.com.tlr.ambev.tech.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.NoCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public CredentialsProvider googleCredentialsProvider() {
        return NoCredentials::getInstance;
    }
}