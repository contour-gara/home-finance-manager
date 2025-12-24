package org.contourgara.presentation

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import org.contourgara.application.CreateExpenseDto
import org.contourgara.application.CreateExpenseParam
import org.contourgara.application.CreateExpenseUseCase
import ulid.ULID

class ExpenseRoutingTest : FunSpec({
    test("支出情報をユースケースに渡し、返り値から支出 ID と支出イベント ID を返す") {
        testApplication {
            // setup
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()
            every {
                createExpenseUseCase
                    .execute(
                        param = CreateExpenseParam(
                            expenseId = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"),
                            amount = 1000,
                            payer = "DIRECT_DEBIT",
                            category = "RENT",
                            memo = "test",
                        )
                    )
            } returns CreateExpenseDto(
                expenseId = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"),
                expenseEventId = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"),
            )

            application {
                configureExpenseRouting(createExpenseUseCase)
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
                      "memo":"test"
                    }
                """.trimIndent())
            }

            // assert
            actual shouldHaveStatus 201
            actual.bodyAsText() shouldBe  "{\"expenseId\":\"01K4MXEKC0PMTJ8FA055N4SH79\",\"expenseEventId\":\"01KD27JEZQQY88RG18034YZHBV\"}"
        }
    }
})
