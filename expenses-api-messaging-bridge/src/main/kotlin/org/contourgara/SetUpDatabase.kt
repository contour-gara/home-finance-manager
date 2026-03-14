package org.contourgara

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

fun setUpDatabase(expensesApiMessagingBridgeConfig: ExpensesApiMessagingBridgeConfig) {
    Flyway
        .configure()
        .dataSource(
            expensesApiMessagingBridgeConfig.datasourceUrl,
            expensesApiMessagingBridgeConfig.datasourceUser,
            expensesApiMessagingBridgeConfig.datasourcePassword,
        )
        .driver("com.mysql.cj.jdbc.Driver")
        .load()
        .migrate()

    Database.connect(
        url = expensesApiMessagingBridgeConfig.datasourceUrl,
        driver = "com.mysql.cj.jdbc.Driver",
        user = expensesApiMessagingBridgeConfig.datasourceUser,
        password = expensesApiMessagingBridgeConfig.datasourcePassword,
    )
}
