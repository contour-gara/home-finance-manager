package org.contourgara

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.testcontainers.mysql.MySQLContainer

class ExpensesApiApplicationTest : FunSpec({
    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }
    mysql.start()

    val wireMockServer = WireMockServer(28080)

    extensions(
        WireMockListener(wireMockServer, ListenerMode.PER_SPEC),
    )

    wireMockServer.stubFor(
        WireMock.get(urlPathEqualTo("/next-ulid"))
            .willReturn(ok("01KD27JEZQQY88RG18034YZHBV"))
    )

    test("health エンドポイントにアクセスすると、'Expenses API is running!' が取得できる") {
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
                    "application.ulid-sequencer.base-url" to "http://localhost:28080",
                )
            }

            // execute
            val actual = client.get("/health")

            // assert
            actual shouldHaveStatus 200
            actual.bodyAsText() shouldBe "Expenses API is running!"
        }
    }

    test("支出登録で、支出情報を受け取り、支出 ID と支出イベント ID を返す") {
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
                    "application.ulid-sequencer.base-url" to "http://localhost:28080",
                )
            }

            // execute
            val actual = client.post("/expense") {
                contentType(ContentType.Application.Json)
                setBody("""
                    {
                      "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                      "amount": 1000,
                      "payer":"DIRECT_DEBIT",
                      "category":"RENT",
                      "year":"2026",
                      "month":"1",
                      "memo":"test"
                    }
                """.trimIndent())
            }

            // assert
            actual shouldHaveStatus 201
            actual.bodyAsText() shouldBe "{\"expenseId\":\"01K4MXEKC0PMTJ8FA055N4SH79\",\"expenseEventId\":\"01KD27JEZQQY88RG18034YZHBV\"}"
        }
    }
})
