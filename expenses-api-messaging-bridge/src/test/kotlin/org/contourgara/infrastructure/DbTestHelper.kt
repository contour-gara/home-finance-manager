package org.contourgara.infrastructure

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.testcontainers.mysql.MySQLContainer

object DbTestHelper {
    fun migrateAndConnect(mysql: MySQLContainer) {
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
                "expense_id",
                "processed_message_id",
            )
        }.launch()
    }
}
