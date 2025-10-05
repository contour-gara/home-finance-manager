package org.contourgara

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.kafka.ConfluentKafkaContainer

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    fun kafkaContainer(): ConfluentKafkaContainer =
        ConfluentKafkaContainer("confluentinc/cp-kafka:8.0.1")
}
