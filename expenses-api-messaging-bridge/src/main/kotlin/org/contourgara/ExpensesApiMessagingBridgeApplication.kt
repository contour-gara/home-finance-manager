package org.contourgara

fun main() {
    println("hello")
    val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig.fromEnvironment()
    KafkaInitializer(expensesApiMessagingBridgeConfig = expensesApiMessagingBridgeConfig).createTopics()
}
