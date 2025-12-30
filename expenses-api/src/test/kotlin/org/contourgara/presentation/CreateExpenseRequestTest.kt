package org.contourgara.presentation

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class CreateExpenseRequestTest : FunSpec({
    test("フィールドが足りない場合、MissingFieldException が発生すること") {
        // setup
        val json = """
            {
                "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                "amount": 1000,
                "payer":"DIRECT_DEBIT",
                "category":"RENT",
                "year":"2026"
            }
        """.trimIndent()

        // execute & assert
        val e = shouldThrowExactly<MissingFieldException> {
            Json.decodeFromString<CreateExpenseRequest>(string = json)
        }
        e.message shouldBe "Fields [month, memo] are required for type with serial name 'org.contourgara.presentation.CreateExpenseRequest', but they were missing at path: \$"
        e.missingFields shouldBe listOf("month", "memo")
    }

    test("Int のフィールドに文字列が入っている場合、SerializationException が発生すること") {
        // setup
        val json = """
            {
                "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                "amount": "a",
                "payer":"DIRECT_DEBIT",
                "category":"RENT",
                "year":"2026",
                "month": "5 月",
                "memo": "test"
            }
        """.trimIndent()

        // execute & assert
        val e = shouldThrow<SerializationException> {
            Json.decodeFromString<CreateExpenseRequest>(string = json)
        }
        e.message shouldBe """
            Unexpected JSON token at offset 63: Unexpected symbol 'a' in numeric literal at path: $.amount
            JSON input: {
                "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                "amount": "a",
                "payer":"DIRECT_DEBIT",
                "category":"RENT",
                "year":"2026",
                "month": "5 月",
                "memo": "test"
            }
        """.trimIndent()
    }

    test("不要なフィールドが入っている場合、SerializationException が発生すること") {
        // setup
        val json = """
            {
                "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                "amount": 1000,
                "payer":"DIRECT_DEBIT",
                "category":"RENT",
                "year":"2026",
                "month": "5",
                "memo":"test",
                "test":"test"
            }
        """.trimIndent()

        // execute & assert
        val e = shouldThrow<SerializationException> {
            Json.decodeFromString<CreateExpenseRequest>(string = json)
        }
        e.message shouldBe """
            Encountered an unknown key 'test' at offset 181 at path: $
            Use 'ignoreUnknownKeys = true' in 'Json {}' builder or '@JsonIgnoreUnknownKeys' annotation to ignore unknown keys.
            JSON input: {
                "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                "amount": 1000,
                "payer":"DIRECT_DEBIT",
                "category":"RENT",
                "year":"2026",
                "month": "5",
                "memo":"test",
                "test":"test"
            }
        """.trimIndent()
    }
})
