package org.contourgara

data class ExpensesApiMessagingBridgeConfig(
    val kafkaBootstrapServer: String,
    val topicName: String = "expenses-api-messaging-bridge",
    val consumerGroupId: String = "expenses-api-messaging-bridge",
    val consumerAutoOffsetReset: String = "earliest",
) {
    companion object {
        fun fromEnvironment(): ExpensesApiMessagingBridgeConfig =
            ExpensesApiMessagingBridgeConfig(
                kafkaBootstrapServer = System.getenv("KAFKA_BOOTSTRAP_SERVERS"),
                consumerAutoOffsetReset = System.getenv("CONSUMER_AUTO_OFFSET_RESET"),
            )
    }
}
