package org.contourgara.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.contourgara.domain.Category
import org.contourgara.domain.EventCategory
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.contourgara.domain.infrastructure.UlidClient
import org.jetbrains.exposed.v1.jdbc.Database
import ulid.ULID

class CreateExpenseUseCaseTest : FunSpec({
    beforeSpec {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
            user = "sa",
            password = "",
        )
    }

    test("支出作成メソッドが、支出保存メソッドと ID 取得メソッドとイベント保存メソッドと合計支出検索メソッドを呼び、支出 ID とイベント ID を返す") {
        // setup
        val param = CreateExpenseParam(
            expenseId = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"),
            amount = 1000,
            payer = "DIRECT_DEBIT",
            category = "RENT",
            year = 2026,
            month = 1,
            memo = "test",
        )

        val expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))
        val expenseEventId = ExpenseEventId(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"))

        val expense = Expense(
            expenseId = expenseId,
            amount = 1000,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            year = Year._2026,
            month = Month.JANUARY,
            memo = "test",
        )

        val expenseEvent = ExpenseEvent(
            expenseEventID = expenseEventId,
            expenseId = expenseId,
            eventCategory = EventCategory.CREATE,
        )

        val expenseRepository = mockk<ExpenseRepository>()
        every { expenseRepository.create(expense) } returns expense

        val ulidClient = mockk<UlidClient>()
        every { ulidClient.nextUlid() } returns expenseEventId

        val expenseEventRepository = mockk<ExpenseEventRepository>()
        every { expenseEventRepository.save(expenseEvent) } returns expenseEvent

        val expensesRepository = mockk<ExpensesRepository>()
        every { expensesRepository.findLatestExpenses(expense.year, expense.month, expense.payer, expense.category) } returns null

        val sut = CreateExpenseUseCase(
            expenseRepository = expenseRepository,
            ulidClient = ulidClient,
            expenseEventRepository = expenseEventRepository,
            expensesRepository = expensesRepository,
        )

        // execute
        val actual = sut.execute(param)

        // assert
        val expected = CreateExpenseDto(
            expenseId = expenseId.id,
            expenseEventId = expenseEventId.id,
        )
        actual shouldBe expected

        verify(exactly = 1) { expenseRepository.create(expense) }
        verify(exactly = 1) { ulidClient.nextUlid() }
        verify(exactly = 1) { expenseEventRepository.save(expenseEvent) }
        verify(exactly = 1) { expensesRepository.findLatestExpenses(expense.year, expense.month, expense.payer, expense.category) }
    }
})
