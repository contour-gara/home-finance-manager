package org.contourgara

import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import org.contourgara.eventlistener.Consumer
import org.contourgara.infrastructure.DiscordMessageClient
import org.contourgara.infrastructure.ExpenseClientImpl
import org.contourgara.infrastructure.ExpenseIdRepositoryImpl
import org.contourgara.infrastructure.ProcessedMessageIdRepositoryImpl

fun main() {
    val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig.fromEnvironment()
    KafkaInitializer(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig).createTopics()
    setUpDatabase(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig)

    Consumer(
        createExpenseUseCase = CreateExpenseUseCase(
            processedMessageIdRepository = ProcessedMessageIdRepositoryImpl,
            expenseClient = ExpenseClientImpl(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig),
            expenseIdRepository = ExpenseIdRepositoryImpl,
            messageClient = DiscordMessageClient(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig),
        ),
        deleteExpenseUseCase = DeleteExpenseUseCase(),
        expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig,
    )
        .listen()
}
