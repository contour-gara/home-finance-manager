package org.contourgara.infrastructure.repository

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.testcontainers.containers.MySQLContainer

object DbTestHelper {
    fun migrateAndConnect(mysql: MySQLContainer<*>) {
        Flyway
            .configure()
            .dataSource(
                mysql.jdbcUrl,
                mysql.username,
                mysql.password,
            )
            .driver("com.mysql.cj.jdbc.Driver")
            .load()
            .migrate()

        Database.connect(
            url = mysql.jdbcUrl,
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql.username,
            password = mysql.password,
        )
    }

    fun deleteAllData(url: String, user: String, password: String) {
        dbSetup(
            to = DriverManagerDestination(url, user, password),
        ) {
            deleteAllFrom(
                "expense_amount",
                "expense_payer",
                "expense_category",
                "expense_year",
                "expense_month",
                "expense_memo",
                "expense_event",
                "expense_event_category",
                "expenses_year",
                "expenses_month",
                "expenses_payer",
                "expenses_category",
                "expenses_amount",
                "expense_event_id",
                "expense_id",
            )
        }.launch()
    }
}
