package org.contourgara

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import org.testcontainers.mysql.MySQLContainer
import ulid.ULID

class UlidSequencerApplicationTest : FunSpec({
    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }
    mysql.start()

    test("health エンドポイントにアクセスすると、'Hello World!' が取得できる") {
        withEnvironment(
            environment = mapOf(
                "ULID_SEQUENCER_DATASOURCE_URL" to mysql.jdbcUrl,
                "DATASOURCE_USERNAME" to mysql.username,
                "DATASOURCE_PASSWORD" to mysql.password,
            ),
            mode = OverrideMode.SetOrOverride,
        ) {
            testApplication {
                // setup
                application {
                    module()
                }

                // execute
                val actual = client.get("/health")

                // assert
                actual shouldHaveStatus 200
                actual.bodyAsText() shouldBe "Hello World!"
            }
        }
    }

    test("next-ulid エンドポイントにアクセスすると、ULID 形式の文字列が取得できる") {
        withEnvironment(
            environment = mapOf(
                "ULID_SEQUENCER_DATASOURCE_URL" to mysql.jdbcUrl,
                "DATASOURCE_USERNAME" to mysql.username,
                "DATASOURCE_PASSWORD" to mysql.password,
            ),
            mode = OverrideMode.SetOrOverride,
        ) {
            testApplication {
                // setup
                application {
                    module()
                }

                // execute
                val actual = client.get("/next-ulid")

                // assert
                actual shouldHaveStatus 200
                ULID.parseULID(actual.bodyAsText()).shouldNotBeNull()
            }
        }
    }
})
