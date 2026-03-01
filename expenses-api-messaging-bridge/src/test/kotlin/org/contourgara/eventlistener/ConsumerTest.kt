package org.contourgara.eventlistener

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerSpecExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.contourgara.ExpensesApiMessagingBridgeConfig
import org.contourgara.KafkaInitializer
import org.contourgara.application.CreateExpenseUseCase
import org.testcontainers.kafka.ConfluentKafkaContainer
import java.time.Duration

class ConsumerTest : FunSpec({
    val confluentKafkaContainer = install(ext = TestContainerSpecExtension(container = ConfluentKafkaContainer("confluentinc/cp-kafka:8.0.1")))
    val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig(kafkaBootstrapServer = confluentKafkaContainer.bootstrapServers)
    KafkaInitializer(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig).createTopics()

    test("トピックを購読できる") {
        // setup
        val producer = KafkaProducer<String, String>(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to confluentKafkaContainer.bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
            )
        )

        val createExpenseUseCase = mockk<CreateExpenseUseCase>()
        every { createExpenseUseCase.execute() } returns Unit

        val sut = Consumer(
            createExpenseUseCase = createExpenseUseCase,
            expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig,
        )

        // execute
        Thread(sut::listen, "kafka-consumer")
            .apply {
                isDaemon = true
                start()
            }

        producer.send(ProducerRecord("expenses-api-messaging-bridge", "key", "{\"billId\": \"01K9HSSXN6VYPGG5E10Q1TFAGF\"}")).get()

        // assert
        await withPollDelay(Duration.ofSeconds(1)) atMost(Duration.ofSeconds(10)) untilAsserted {
            verify(exactly = 1) { createExpenseUseCase.execute() }
        }
    }
})
