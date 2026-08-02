package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import kotlinx.datetime.LocalDate

class ExpenseTest : FunSpec({
    test("ファクトリーメソッドでバリデーションエラーがある場合、蓄積されて返る") {
        // execute
        val actual = Expense.of(
            expenseId = "test",
            amount = -100,
            payer = "test",
            category = "test",
            date = LocalDate(year = 2025, month = 1, day = 1),
            memo = "",
        )

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 6
        }
    }
})
