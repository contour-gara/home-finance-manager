package org.contourgara.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.contourgara.domain.Amount
import org.contourgara.domain.Category
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.Expenses
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.jetbrains.exposed.v1.jdbc.Database
import ulid.ULID

class MonthlyExpensesQueryServiceTest : FunSpec({
    beforeSpec {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
            user = "sa",
            password = "",
        )
    }

    test("年月から、月の合計支出を算出できる") {
        // setup
        val expensesRepository = mockk<ExpensesRepository>()
        every {
            expensesRepository.findMonthlyExpenses(year = Year._2026, month = Month.JANUARY)
        } returns listOf(
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
                amount = Amount(value = 1000),
            ),
        )

        val sut = MonthlyExpensesQueryService(
            expensesRepository = expensesRepository,
        )

        // execute
        val actual = sut.execute(year = 2026, month = 1)

        // assert
        val expected = Pair(
            first = mapOf(
                "UTILITIES" to 1000,
                "RENT" to 2500,
            ),
            second = 3500,
        )
        actual shouldBe expected
    }

    test("年月と支払い者から、支払い者の月の合計支出を算出できる") {
        // setup
        val expensesRepository = mockk<ExpensesRepository>()
        every {
            expensesRepository.findMonthlyExpenses(year = Year._2026, month = Month.JANUARY)
        } returns listOf(
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
                amount = Amount(value = 1000),
            ),
        )

        val sut = MonthlyExpensesQueryService(
            expensesRepository = expensesRepository,
        )

        // execute
        val actual = sut.execute(year = 2026, month = 1, payer = "DIRECT_DEBIT")

        // assert
        val expected = Pair(
            first = mapOf(
                "UTILITIES" to 1000,
                "RENT" to 1500,
            ),
            second = 2500,
        )
        actual shouldBe expected
    }
})
