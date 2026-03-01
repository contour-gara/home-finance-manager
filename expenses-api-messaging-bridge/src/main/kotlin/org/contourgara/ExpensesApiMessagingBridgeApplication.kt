package org.contourgara

import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.eventlistener.Consumer

fun main() {
    val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig.fromEnvironment()
    KafkaInitializer(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig).createTopics()

    Consumer(
        createExpenseUseCase = CreateExpenseUseCase(),
        expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig,
    )
        .listen()
}
