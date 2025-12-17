package org.contourgara

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.testcontainers.containers.MySQLContainer

class ExpensesApiApplicationTest : FunSpec({
    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }
    mysql.start()

    test("ルートエンドポイントにアクセスすると、'Expenses API is running!' が取得できる") {
        testApplication {
            // setup
            application {
                module()
            }

            environment {
                config = MapApplicationConfig(
                    "application.datasource.url" to mysql.jdbcUrl,
                    "application.datasource.username" to mysql.username,
                    "application.datasource.password" to mysql.password,
                )
            }

            // execute
            val actual = client.get("/")

            // assert
            actual shouldHaveStatus 200
            actual.bodyAsText() shouldBe "Expenses API is running!"
        }
    }
})
