package org.contourgara.presentation

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import org.contourgara.application.CreateExpenseDto
import org.contourgara.application.CreateExpenseParam
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import ulid.ULID

class ExpenseRoutingTest : FunSpec({
    test("支出情報をユースケースに渡し、返り値から支出 ID と支出イベント ID を返す") {
        testApplication {
            // setup
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()
            every {
                createExpenseUseCase
                    .execute(
                        param = CreateExpenseParam(
                            expenseId = "01K4MXEKC0PMTJ8FA055N4SH79",
                            amount = 1000,
                            payer = "DIRECT_DEBIT",
                            category = "RENT",
                            year = 2026,
                            month = 1,
                            memo = "test",
                        ),
                    )
            } returns CreateExpenseDto(
                expenseId = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"),
                expenseEventId = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"),
            )

            application {
                install(plugin = ContentNegotiation) {
                    json()
                }
                configureExpenseRouting(
                    createExpenseUseCase = createExpenseUseCase,
                    deleteExpenseUseCase = deleteExpenseUseCase,
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
            actual.bodyAsText() shouldBe  "{\"expenseId\":\"01K4MXEKC0PMTJ8FA055N4SH79\",\"expenseEventId\":\"01KD27JEZQQY88RG18034YZHBV\"}"
        }
    }

    test("支出 ID を支出削除ユースケースに渡し、204 を返す") {
        testApplication {
            // setup
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()
            every {
                deleteExpenseUseCase
                    .execute(
                        expenseId = "01K4MXEKC0PMTJ8FA055N4SH79",
                    )
            } returns ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")

            application {
                install(plugin = ContentNegotiation) {
                    json()
                }
                configureExpenseRouting(
                    createExpenseUseCase = createExpenseUseCase,
                    deleteExpenseUseCase = deleteExpenseUseCase,
                )
            }

            // execute
            val actual = client.delete("/expense/01K4MXEKC0PMTJ8FA055N4SH79")

            // assert
            actual shouldHaveStatus 200
            actual.bodyAsText() shouldBe """{"expenseEventId":"01KD27JEZQQY88RG18034YZHBV"}"""
        }
    }
})
