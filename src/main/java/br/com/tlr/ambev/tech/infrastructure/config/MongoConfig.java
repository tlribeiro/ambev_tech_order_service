package br.com.tlr.ambev.tech.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "br.com.tlr.ambev.tech.infrastructure.adapters.out.repositories")
public class MongoConfig {
}