package org.contourgara

data class ExpensesApiMessagingBridgeConfig(
    val datasourceUrl: String,
    val datasourceUser: String,
    val datasourcePassword: String,
    val expensesApiBaseUrl: String,
    val kafkaBootstrapServer: String,
    val consumerAutoOffsetReset: String,
    val topicName: String = "expenses-api-messaging-bridge",
    val consumerGroupId: String = "expenses-api-messaging-bridge",
) {
    companion object {
        fun fromEnvironment(): ExpensesApiMessagingBridgeConfig =
            ExpensesApiMessagingBridgeConfig(
                datasourceUrl = System.getenv("DATASOURCE_URL"),
                datasourceUser = System.getenv("DATASOURCE_USERNAME"),
                datasourcePassword = System.getenv("DATASOURCE_PASSWORD"),
                expensesApiBaseUrl = System.getenv("EXPENSE_API_BASE_URL"),
                kafkaBootstrapServer = System.getenv("KAFKA_BOOTSTRAP_SERVERS"),
                consumerAutoOffsetReset = System.getenv("CONSUMER_AUTO_OFFSET_RESET"),
            )
    }
}
