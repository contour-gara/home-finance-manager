package org.contourgara.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ulid.ULID

class ExpensesTest : FunSpec({
    test("合計支出を更新できる") {
        // setup
        val expenses = Expenses(
            lastEventId = ExpenseEventId(id = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            amount = 1000,
        )

        val expense = Expense(
            expenseId = ExpenseId(id = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = 500,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = "test1",
        )

        val expenseEventId = ExpenseEventId(id = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y"))

        // execute
        val actual = Expenses.from(expenses, expense, expenseEventId)

        // assert
        val expected = Expenses(
            lastEventId = ExpenseEventId(id = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y")),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            amount = 1500,
        )
        actual shouldBe expected
    }
})
