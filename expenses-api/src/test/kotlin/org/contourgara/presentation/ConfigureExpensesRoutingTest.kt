package org.contourgara.presentation

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import org.contourgara.application.MonthlyExpensesQueryService

class ConfigureExpensesRoutingTest : FunSpec({
    test("月の合計支出を、年月から検索できる") {
        testApplication {
            // setup
            val monthlyExpensesQueryService = mockk<MonthlyExpensesQueryService>()
            every {
                monthlyExpensesQueryService
                    .execute(
                        year = 2026,
                        month = 1,
                    )
            } returns Pair(
                first = mapOf(
                    "UTILITIES" to 1000,
                    "RENT" to 2500,
                ),
                second = 3500,
            )

            application {
                install(plugin = ContentNegotiation) {
                    json()
                }
                configureExpensesRouting(
                    monthlyExpensesQueryService = monthlyExpensesQueryService,
                )
            }

            // execute
            val actual = client.get(urlString = "/expenses/2026/1")

            // assert
            actual shouldHaveStatus 200
            actual.bodyAsText() shouldBe  "{\"breakdown\":{\"UTILITIES\":1000,\"RENT\":2500},\"total\":3500}"
        }
    }

    test("支払い者ごとの月の合計支出を、年月と支払い者から検索できる") {
        testApplication {
            // setup
            val monthlyExpensesQueryService = mockk<MonthlyExpensesQueryService>()
            every {
                monthlyExpensesQueryService
                    .execute(
                        year = 2026,
                        month = 1,
                        payer = "DIRECT_DEBIT"
                    )
            } returns Pair(
                first = mapOf(
                    "UTILITIES" to 1000,
                    "RENT" to 1500,
                ),
                second = 2500,
            )

            application {
                install(plugin = ContentNegotiation) {
                    json()
                }
                configureExpensesRouting(
                    monthlyExpensesQueryService = monthlyExpensesQueryService,
                )
            }

            // execute
            val actual = client.get(urlString = "/expenses/2026/1?payer=DIRECT_DEBIT")

            // assert
            actual shouldHaveStatus 200
            actual.bodyAsText() shouldBe "{\"breakdown\":{\"UTILITIES\":1000,\"RENT\":1500},\"total\":2500}"
        }
    }
})
