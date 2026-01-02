package org.contourgara.application

import arrow.core.Either
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.contourgara.domain.Amount
import org.contourgara.domain.Category
import org.contourgara.domain.EventCategory
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseAlreadyDeletedException
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.ExpenseNotFoundError
import org.contourgara.domain.ExpenseNotFoundException
import org.contourgara.domain.Expenses
import org.contourgara.domain.Memo
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpenseEventIdClient
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.jetbrains.exposed.v1.jdbc.Database
import ulid.ULID

class DeleteExpenseUseCaseTest : FunSpec({
    beforeSpec {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
            user = "sa",
            password = "",
        )
    }

    test("支出削除メソッドが、削除イベントの ULID を返す") {
        // setup
        val expenseRepository = mockk<ExpenseRepository>()
        val expenseEventRepository = mockk<ExpenseEventRepository>()
        val expensesRepository = mockk<ExpensesRepository>()
        val expenseEventIdClient = mockk<ExpenseEventIdClient>()
        val sut = DeleteExpenseUseCase(
            expenseRepository = expenseRepository,
            expenseEventRepository = expenseEventRepository,
            expensesRepository = expensesRepository,
            expenseEventIdClient = expenseEventIdClient,
        )

        val expenseId = ExpenseId(value = "01K4MXEKC0PMTJ8FA055N4SH79")
        val expenseEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV"))
        val deleteEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDHVD5XTTR9XR4ZAFSSETGXS"))
        val expenseEvent = ExpenseEvent(
            expenseEventId = expenseEventId,
            expenseId = expenseId,
            eventCategory = EventCategory.CREATE,
        )
        val deleteExpenseEvent = ExpenseEvent(
            expenseEventId = deleteEventId,
            expenseId = expenseId,
            eventCategory = EventCategory.DELETE,
        )
        val expense = Expense(
            expenseId = expenseId,
            amount = Amount(value = 1000),
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = Memo(value = "test")
        )
        val beforeExpenses = Expenses(
            lastEventId = expenseEventId,
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 1500),
        )
        val afterExpenses = Expenses(
            lastEventId = deleteEventId,
            year = Year._2026,
            month = Month.JANUARY,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            amount = Amount(value = 500),
        )
        every { expenseEventRepository.findByExpenseId(expenseId = expenseId) } returns Either.Right(value = expenseEvent)
        every { expenseEventIdClient.nextExpensesEventId() } returns deleteEventId
        every { expenseEventRepository.save(expenseEvent = deleteExpenseEvent) } returns deleteExpenseEvent
        every { expenseRepository.findById(expenseId = expenseId) } returns expense
        every { expensesRepository.findLatestExpenses(year = expense.year, month = expense.month, payer = expense.payer, category = expense.category) } returns beforeExpenses
        every { expensesRepository.save(expenses = afterExpenses) } returns afterExpenses

        // execute
        val actual = sut.execute(expenseId = "01K4MXEKC0PMTJ8FA055N4SH79")

        // assert
        val expected = deleteEventId.value
        actual shouldBe expected
    }

    test("支出削除メソッドが、支出イベントが存在しない場合、ExpenseNotFoundException を投げる") {
        // setup
        val expenseRepository = mockk<ExpenseRepository>()
        val expenseEventRepository = mockk<ExpenseEventRepository>()
        val expensesRepository = mockk<ExpensesRepository>()
        val expenseEventIdClient = mockk<ExpenseEventIdClient>()
        val sut = DeleteExpenseUseCase(
            expenseRepository = expenseRepository,
            expenseEventRepository = expenseEventRepository,
            expensesRepository = expensesRepository,
            expenseEventIdClient = expenseEventIdClient,
        )

        val expenseId = ExpenseId(value = "01K4MXEKC0PMTJ8FA055N4SH79")
        every { expenseEventRepository.findByExpenseId(expenseId = expenseId) } returns Either.Left(value = ExpenseNotFoundError(expenseId = "01K4MXEKC0PMTJ8FA055N4SH79"))

        // execute & assert
        shouldThrowExactly<ExpenseNotFoundException> {
            sut.execute(expenseId = "01K4MXEKC0PMTJ8FA055N4SH79")
        }.message shouldBe "Expense was not found: [The expense with the given ID was not found.: expenseId = 01K4MXEKC0PMTJ8FA055N4SH79]"
    }

    test("支出削除メソッドが、支出イベントが削除済みの場合、ExpenseAlreadyDeletedException を投げる") {
        // setup
        val expenseRepository = mockk<ExpenseRepository>()
        val expenseEventRepository = mockk<ExpenseEventRepository>()
        val expensesRepository = mockk<ExpensesRepository>()
        val expenseEventIdClient = mockk<ExpenseEventIdClient>()
        val sut = DeleteExpenseUseCase(
            expenseRepository = expenseRepository,
            expenseEventRepository = expenseEventRepository,
            expensesRepository = expensesRepository,
            expenseEventIdClient = expenseEventIdClient,
        )

        val expenseId = ExpenseId(value = "01K4MXEKC0PMTJ8FA055N4SH79")
        val expenseEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDHVD5XTTR9XR4ZAFSSETGXS"))
        val deleteEventId = ExpenseEventId(value = ULID.parseULID(ulidString = "01KDYJFY2W5B9TJ290NWMP6EP8"))
        val expenseEvent = ExpenseEvent(
            expenseEventId = expenseEventId,
            expenseId = expenseId,
            eventCategory = EventCategory.DELETE,
        )
        every { expenseEventRepository.findByExpenseId(expenseId = expenseId) } returns Either.Right(value = expenseEvent)
        every { expenseEventIdClient.nextExpensesEventId() } returns deleteEventId

        // execute & assert
        shouldThrowExactly<ExpenseAlreadyDeletedException> {
            sut.execute(expenseId = "01K4MXEKC0PMTJ8FA055N4SH79")
        }.message shouldBe "Expense was already deleted: [The expense with the given ID was already deleted.: expenseId = 01K4MXEKC0PMTJ8FA055N4SH79]"
    }
})
