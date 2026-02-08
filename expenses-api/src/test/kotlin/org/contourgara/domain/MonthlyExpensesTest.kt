package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ulid.ULID

class MonthlyExpensesTest : FunSpec({
    test("合計支出のリストから、月の合計支出を作成できる") {
        // setup
        val expenses = listOf(
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJNKH74MRSVZJGVBG1PJA4V")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.UTILITIES,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJN9CGGE9G24AZTQHQKP97T")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.GARA,
                category = Category.RENT,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDHVD5XTTR9XR4ZAFSSETGXS")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1500),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1),
            ),
        )

        // execute
        val actual = MonthlyExpenses.from(expenses = expenses)

        // assert
        val expected = MonthlyExpenses(
            values = mapOf(
                Category.UTILITIES to Amount(value = 1000),
                Category.RENT to Amount(value = 2500),
            )
        )
        actual shouldBe expected
    }

    test("月の合計支出の算出において、年が異なるものがある場合例外を投げる") {
        // setup
        val expenses = listOf(
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJNKH74MRSVZJGVBG1PJA4V")),
                year = Year._2027,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.UTILITIES,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJN9CGGE9G24AZTQHQKP97T")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.GARA,
                category = Category.RENT,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDHVD5XTTR9XR4ZAFSSETGXS")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1500),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1),
            ),
        )

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            MonthlyExpenses.from(expenses = expenses)
        }.message shouldBe "異なる年のデータがあります。"
    }

    test("月の合計支出の算出において、月が異なるものがある場合例外を投げる") {
        // setup
        val expenses = listOf(
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJNKH74MRSVZJGVBG1PJA4V")),
                year = Year._2026,
                month = Month.FEBRUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.UTILITIES,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDJN9CGGE9G24AZTQHQKP97T")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.GARA,
                category = Category.RENT,
                amount = Amount(value = 1000),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDHVD5XTTR9XR4ZAFSSETGXS")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1500),
            ),
            Expenses(
                lastEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
                year = Year._2026,
                month = Month.JANUARY,
                payer = Payer.DIRECT_DEBIT,
                category = Category.RENT,
                amount = Amount(value = 1),
            ),
        )

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            MonthlyExpenses.from(expenses = expenses)
        }.message shouldBe "異なる年のデータがあります。"
    }
})
