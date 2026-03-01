package org.contourgara

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.errors.TopicExistsException
import org.slf4j.LoggerFactory
import java.util.Optional

class KafkaInitializer(
    private val expensesApiMessagingBridgeConfig: ExpensesApiMessagingBridgeConfig,
) {
    private val log = LoggerFactory.getLogger(KafkaInitializer::class.java)

    fun createTopics() =
        AdminClient
            .create(
                mapOf(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to expensesApiMessagingBridgeConfig.kafkaBootstrapServer
                ),
            )
            .use { admin ->
                admin
                    .createTopics(
                        listOf(
                            NewTopic(expensesApiMessagingBridgeConfig.topicName, Optional.empty(), Optional.empty()),
                        ),
                    )
                    .values()
                    .forEach { (topicName, future) ->
                        runCatching { future.get() }
                            .onSuccess { log.debug("Created: $topicName") }
                            .onFailure { e ->
                                if (e.cause is TopicExistsException) log.debug("Already exists: $topicName")
                            }
                    }
            }
}
