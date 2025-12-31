package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ulid.ULID

class ExpensesTest : FunSpec({
    test("合計支出を更新できる") {
        // setup
        val expenses = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1000),
        )

        val expense = Expense(
            expenseId = ExpenseId(value = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = Amount(value = 500),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test1"),
        )

        val expenseEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y"))

        // execute
        val actual = Expenses.from(expenses, expense, expenseEventId)

        // assert
        val expected = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1500),
        )
        actual shouldBe expected
    }

    test("合計支出が存在しない場合、合計支出を作成する") {
        // setup
        val expenses = null

        val expense = Expense(
            expenseId = ExpenseId(value = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = Amount(value = 500),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test1"),
        )

        val expenseEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y"))

        // execute
        val actual = Expenses.from(expenses, expense, expenseEventId)

        // assert
        val expected = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 500),
        )
        actual shouldBe expected
    }

    test("支出合計のイベント ID よりもイベント ID が古い場合、例外を投げる") {
        // setup
        val expenses = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1000),
        )

        val expense = Expense(
            expenseId = ExpenseId(value = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = Amount(value = 500),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test1"),
        )

        val expenseEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"))

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Expenses.from(expenses, expense, expenseEventId)
        }.message shouldBe "expenseEventId must be greater than lastEventId: expenseEventId = 01KD27JEZQQY88RG18034YZHBV, lastEventId = 01KDJBSGQSV39NEGEQGXPH350Y"
    }

    test("支出合計と支出の年が違う場合、例外を投げる") {
        // setup
        val expenses = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            year = Year._2027,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1000),
        )

        val expense = Expense(
            expenseId = ExpenseId(value = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = Amount(value = 500),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test1"),
        )

        val expenseEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y"))

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Expenses.from(expenses, expense, expenseEventId)
        }.message shouldBe "year must be same: expenses.year = 2027, expense.year = 2026"
    }

    test("支出合計と支出の月が違う場合、例外を投げる") {
        // setup
        val expenses = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            year = Year._2026,
            month = Month.FEBRUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1000),
        )

        val expense = Expense(
            expenseId = ExpenseId(value = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = Amount(value = 500),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test1"),
        )

        val expenseEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y"))

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Expenses.from(expenses, expense, expenseEventId)
        }.message shouldBe "month must be same: expenses.month = 2, expense.month = 1"
    }

    test("支出合計と支出の支出者が違う場合、例外を投げる") {
        // setup
        val expenses = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.GARA,
            category = Category.RENT,
            amount = Amount(value = 1000),
        )

        val expense = Expense(
            expenseId = ExpenseId(value = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = Amount(value = 500),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test1"),
        )

        val expenseEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y"))

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Expenses.from(expenses, expense, expenseEventId)
        }.message shouldBe "payer must be same: expenses.payer = GARA, expense.payer = DIRECT_DEBIT"
    }

    test("支出合計と支出の支出カテゴリが違う場合、例外を投げる") {
        // setup
        val expenses = Expenses(
            lastEventId = ExpenseEventId(value = ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.UTILITIES,
            amount = Amount(value = 1000),
        )

        val expense = Expense(
            expenseId = ExpenseId(value = ULID.parseULID("01KDJ042GNBA94CWB2F151W2SE")),
            amount = Amount(value = 500),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test1"),
        )

        val expenseEventId = ExpenseEventId(value = ULID.parseULID("01KDJBSGQSV39NEGEQGXPH350Y"))

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Expenses.from(expenses, expense, expenseEventId)
        }.message shouldBe "category must be same: expenses.category = UTILITIES, expense.category = RENT"
    }
})
