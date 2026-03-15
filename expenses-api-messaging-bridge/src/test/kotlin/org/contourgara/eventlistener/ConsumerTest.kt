package org.contourgara.eventlistener

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerSpecExtension
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.StringSerializer
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.contourgara.ExpensesApiMessagingBridgeConfig
import org.contourgara.KafkaInitializer
import org.contourgara.application.CreateExpenseParam
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseParam
import org.contourgara.application.DeleteExpenseUseCase
import org.testcontainers.kafka.ConfluentKafkaContainer
import java.time.Duration

class ConsumerTest : FunSpec({
    val confluentKafkaContainer = install(ext = TestContainerSpecExtension(container = ConfluentKafkaContainer("confluentinc/cp-kafka:8.0.1")))

    val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig(
        datasourceUrl = "test",
        datasourceUser = "test",
        datasourcePassword = "test",
        expensesApiBaseUrl = "test",
        discordBotToken = "test",
        discordChannelId = "test",
        kafkaBootstrapServer = confluentKafkaContainer.bootstrapServers,
        consumerAutoOffsetReset = "earliest",
    )
    KafkaInitializer(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig).createTopics()

    val producer = KafkaProducer<String, String>(
        mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to confluentKafkaContainer.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
        )
    )

    val createExpenseUseCase = mockk<CreateExpenseUseCase>()
    val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()

    beforeSpec {
        every { createExpenseUseCase.execute(param = any()) } returns Unit
        every { deleteExpenseUseCase.execute(param = any()) } returns Unit

        val sut = Consumer(
            createExpenseUseCase = createExpenseUseCase,
            deleteExpenseUseCase = deleteExpenseUseCase,
            expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig,
        )

        Thread(sut::listen, "kafka-consumer").apply {
            isDaemon = true
            start()
        }
    }

    beforeTest {
        clearMocks(createExpenseUseCase, deleteExpenseUseCase, answers = false)
    }

    test("ヘッダーが作成の場合、支出作成ユースケースを実行する") {
        // execute
        producer.send(
            ProducerRecord(
                "expenses-api-messaging-bridge",
                null,
                "key",
                """
                    {
                        "messageId": "1477993825762017321",
                        "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                        "amount": 1000,
                        "payer":"DIRECT_DEBIT",
                        "category":"RENT",
                        "year":"2026",
                        "month":"1",
                        "memo":"test"
                    }
                """.trimIndent(),
                listOf(
                    RecordHeader("event-type", "create".toByteArray()),
                ),
            )
        ).get()

        // assert
        await withPollDelay(Duration.ofSeconds(1)) atMost(Duration.ofSeconds(10)) untilAsserted {
            verify(exactly = 1) {
                createExpenseUseCase
                    .execute(param =
                        CreateExpenseParam(
                            messageId = "1477993825762017321",
                            expenseId = "01K4MXEKC0PMTJ8FA055N4SH79",
                            amount = 1000,
                            payer = "DIRECT_DEBIT",
                            category = "RENT",
                            year = 2026,
                            month = 1,
                            memo = "test",
                        )
                    )
            }
            verify(exactly = 0) { deleteExpenseUseCase.execute(param = any()) }
        }
    }

    test("ヘッダーが削除の場合、支出削除ユースケースを実行する") {
        // execute
        producer.send(
            ProducerRecord(
                "expenses-api-messaging-bridge",
                null,
                "key",
                "{\"messageId\": \"1477993825762017321\"}",
                listOf(
                    RecordHeader("event-type", "delete".toByteArray()),
                ),
            )
        ).get()

        // assert
        await withPollDelay(Duration.ofSeconds(1)) atMost(Duration.ofSeconds(10)) untilAsserted {
            verify(exactly = 0) { createExpenseUseCase.execute(param = any()) }
            verify(exactly = 1) { deleteExpenseUseCase.execute(param = DeleteExpenseParam(messageId = "1477993825762017321")) }
        }
    }

    test("無効なヘッダーの場合、何もしない") {
        // execute
        producer.send(ProducerRecord("expenses-api-messaging-bridge", "key", "{\"billId\": \"01K9HSSXN6VYPGG5E10Q1TFAGF\"}")).get()

        // assert
        await withPollDelay(Duration.ofSeconds(1)) atMost(Duration.ofSeconds(10)) untilAsserted {
            verify(exactly = 0) { createExpenseUseCase.execute(param = any()) }
            verify(exactly = 0) { deleteExpenseUseCase.execute(param = any()) }
        }
    }
})
