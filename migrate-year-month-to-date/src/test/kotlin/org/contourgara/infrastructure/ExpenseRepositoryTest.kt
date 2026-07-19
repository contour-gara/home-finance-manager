package org.contourgara.infrastructure

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerSpecExtension
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.testcontainers.mysql.MySQLContainer

class ExpenseRepositoryTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val mysql = install(ext = TestContainerSpecExtension(container = MySQLContainer("mysql:8.0.43-oraclelinux9")))
        .apply { startupAttempts = 1 }

    beforeSpec {
        Flyway
            .configure()
            .dataSource(
                mysql.jdbcUrl + "?rewriteBatchedStatements=TRUE",
                mysql.username,
                mysql.password,
            )
            .driver("com.mysql.cj.jdbc.Driver")
            .load()
            .migrate()

        Database.connect(
            url = mysql.jdbcUrl + "?rewriteBatchedStatements=TRUE",
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql.username,
            password = mysql.password,
        )

        assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl + "?rewriteBatchedStatements=TRUE", mysql.username, mysql.password).create()
    }

    beforeTest {
        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl + "?rewriteBatchedStatements=TRUE", mysql.username, mysql.password),
        ) {
            deleteAllFrom(
                "expense_year",
                "expense_month",
                "expense_memo",
                "expense_date",
                "expense_id",
            )
        }.launch()
    }

    context("日付の保存") {
        test("動確") {
            print("動確")
        }
    }
})