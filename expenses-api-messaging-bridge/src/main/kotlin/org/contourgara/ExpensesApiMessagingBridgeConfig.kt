package org.contourgara

data class ExpensesApiMessagingBridgeConfig(
    val kafkaBootstrapServer: String,
    val topicName: String = "expenses-api-messaging-bridge",
) {
    companion object {
        fun fromEnvironment(): ExpensesApiMessagingBridgeConfig =
            ExpensesApiMessagingBridgeConfig(
                kafkaBootstrapServer = System.getenv("KAFKA_BOOTSTRAP_SERVERS"),
            )
    }
}
