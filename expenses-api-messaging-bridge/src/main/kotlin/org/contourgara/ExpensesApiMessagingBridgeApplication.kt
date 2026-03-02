package org.contourgara

import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import org.contourgara.eventlistener.Consumer

fun main() {
    val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig.fromEnvironment()
    KafkaInitializer(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig).createTopics()

    Consumer(
        createExpenseUseCase = CreateExpenseUseCase(),
        deleteExpenseUseCase = DeleteExpenseUseCase(),
        expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig,
    )
        .listen()
}
