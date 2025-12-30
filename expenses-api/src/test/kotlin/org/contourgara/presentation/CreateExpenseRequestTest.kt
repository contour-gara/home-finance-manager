package org.contourgara.presentation

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class CreateExpenseRequestTest : FunSpec({
    test("json パースできなかった場合の例外") {
        // setup
        val json = """
            
            {
                "expenseId": "01K4MXEKC0PMTJ8FA055N4SH79",
                "amount": 1000,
                "payer":"DIRECT_DEBIT",
                "category":"RENT",
                "year":"2026",
                "month":"1"
            }
        """.trimIndent()

        // execute & assert
        shouldThrow<SerializationException> {
            Json.decodeFromString<CreateExpenseRequest>(string = json)
        }.message shouldBe "Field 'memo' is required for type with serial name 'org.contourgara.presentation.CreateExpenseRequest', but it was missing at path: \$"
    }
})
