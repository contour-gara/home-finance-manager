package org.contourgara.eventlistener

import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.contourgara.ExpensesApiMessagingBridgeConfig
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import org.slf4j.LoggerFactory
import java.time.Duration

class Consumer(
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val expensesApiMessagingBridgeConfig: ExpensesApiMessagingBridgeConfig,
) {
    private val log = LoggerFactory.getLogger(Consumer::class.java)

    fun listen() =
        KafkaConsumer<String, String>(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to expensesApiMessagingBridgeConfig.kafkaBootstrapServer,
                ConsumerConfig.GROUP_ID_CONFIG to expensesApiMessagingBridgeConfig.consumerGroupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to expensesApiMessagingBridgeConfig.consumerAutoOffsetReset,
            )
        )
            .use { consumer ->
                consumer.subscribe(listOf(expensesApiMessagingBridgeConfig.topicName))
                while (true) {
                    consumer.poll(Duration.ofMillis(1000))
                        .forEach { record ->
                            when (val eventType = record.headers().lastHeader("event-type")?.value()?.let { String(bytes = it) }) {
                                "create" -> createExpenseUseCase
                                    .execute(
                                        param = Json.decodeFromString<CreateExpenseRequest>(string = record.value()).toParam(),
                                    )
                                "delete" -> deleteExpenseUseCase.execute()
                                else -> log.debug("Unknown event-type: $eventType")
                            }
                        }
                }
            }
}
