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
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import org.contourgara.domain.ExpenseAlreadyDeletedException
import org.contourgara.domain.ExpenseNotFoundException
import org.contourgara.domain.ValidationException

class GlobalExceptionHandlerTest : FunSpec({
    test("リクエストのフィールド不足で BadRequestException が発生した場合、ステータス 400 と適切なエラーレスポンスが返却される") {
        testApplication {
            // setup
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()

            application {
                configureExpenseRouting(createExpenseUseCase = createExpenseUseCase, deleteExpenseUseCase = deleteExpenseUseCase)
                configureGlobalExceptionHandler()
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
                      "year":"2026"
                    }
                """.trimIndent())
            }

            // assert
            actual shouldHaveStatus 400
            actual.bodyAsText() shouldBe
                    """{"type":"kotlinx.serialization.MissingFieldException","title":"Missing Field Error","errors":[{"detail":"Missing required field: month"},{"detail":"Missing required field: memo"}]}"""
        }
    }

    test("SerializationException が発生した場合、ステータス 400 と適切なエラーレスポンスが返却される") {
        testApplication {
            // setup
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()

            application {
                configureExpenseRouting(createExpenseUseCase = createExpenseUseCase, deleteExpenseUseCase = deleteExpenseUseCase)
                configureGlobalExceptionHandler()
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
                      "month": "5",
                      "memo":"test",
                      "test":"test
                    }
                """.trimIndent())
            }

            // assert
            actual shouldHaveStatus 400
            actual.bodyAsText() shouldBe
                    """{"type":"kotlinx.serialization.json.internal.JsonDecodingException","title":"Serialization Error","errors":[{"detail":"Encountered an unknown key 'test' at offset 165 at path: $\nUse 'ignoreUnknownKeys = true' in 'Json {}' builder or '@JsonIgnoreUnknownKeys' annotation to ignore unknown keys.\nJSON input: {\n  \"expenseId\": \"01K4MXEKC0PMTJ8FA055N4SH79\",\n  \"amount\": 1000,\n  \"payer\":\"DIRECT_DEBIT\",\n  \"category\":\"RENT\",\n  \"year\":\"2026\",\n  \"month\": \"5\",\n  \"memo\":\"test\",\n  \"test\":\"test\n}"}]}"""
        }
    }

    test("ValidationException が発生した場合、ステータス 400 と適切なエラーレスポンスが返却される") {
        testApplication {
            // setup
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()
            every { createExpenseUseCase.execute(any()) }throws
                    ValidationException(
                        title = "test",
                        errors = listOf("test"),
                    )

            application {
                configureExpenseRouting(createExpenseUseCase = createExpenseUseCase, deleteExpenseUseCase = deleteExpenseUseCase)
                configureGlobalExceptionHandler()
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
                      "month": "5",
                      "memo":"test"
                    }
                """.trimIndent())
            }

            // assert
            actual shouldHaveStatus 400
            actual.bodyAsText() shouldBe
                    """{"type":"org.contourgara.domain.ValidationException","title":"test","errors":[{"detail":"test"}]}"""
        }
    }

    test("ExpenseNotFoundException が発生した場合、ステータス 404 と適切なエラーレスポンスが返却される") {
        testApplication {
            // setup
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()
            every { deleteExpenseUseCase.execute(expenseId = "01K4MXEKC0PMTJ8FA055N4SH79") } throws ExpenseNotFoundException(title = "test", errors = listOf("test"))
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()

            application {
                configureExpenseRouting(createExpenseUseCase = createExpenseUseCase, deleteExpenseUseCase = deleteExpenseUseCase)
                configureGlobalExceptionHandler()
            }

            // execute
            val actual = client.delete("/expense/01K4MXEKC0PMTJ8FA055N4SH79")

            // assert
            actual shouldHaveStatus 404
            actual.bodyAsText() shouldBe
                    """{"type":"org.contourgara.domain.ExpenseNotFoundException","title":"test","errors":[{"detail":"test"}]}"""
        }
    }

    test("ExpenseAlreadyDeletedException が発生した場合、ステータス 400 と適切なエラーレスポンスが返却される") {
        testApplication {
            // setup
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()
            every { deleteExpenseUseCase.execute(expenseId = "01K4MXEKC0PMTJ8FA055N4SH79") } throws ExpenseAlreadyDeletedException(title = "test", errors = listOf("test"))
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()

            application {
                configureExpenseRouting(createExpenseUseCase = createExpenseUseCase, deleteExpenseUseCase = deleteExpenseUseCase)
                configureGlobalExceptionHandler()
            }

            // execute
            val actual = client.delete("/expense/01K4MXEKC0PMTJ8FA055N4SH79")

            // assert
            actual shouldHaveStatus 400
            actual.bodyAsText() shouldBe
                    """{"type":"org.contourgara.domain.ExpenseAlreadyDeletedException","title":"test","errors":[{"detail":"test"}]}"""
        }
    }

    test("RuntimeException が発生した場合、ステータス 500 と適切なエラーレスポンスが返却される") {
        testApplication {
            // setup
            val deleteExpenseUseCase = mockk<DeleteExpenseUseCase>()
            val createExpenseUseCase = mockk<CreateExpenseUseCase>()
            every { createExpenseUseCase.execute(any()) } throws RuntimeException("test")

            application {
                configureExpenseRouting(createExpenseUseCase = createExpenseUseCase, deleteExpenseUseCase = deleteExpenseUseCase)
                configureGlobalExceptionHandler()
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
                      "month": "5",
                      "memo":"test"
                    }
                """.trimIndent())
            }

            // assert
            actual shouldHaveStatus 500
            actual.bodyAsText() shouldBe
                    """{"type":"java.lang.RuntimeException","title":"An unexpected error occurred","errors":[{"detail":"test"}]}"""
        }
    }
})
