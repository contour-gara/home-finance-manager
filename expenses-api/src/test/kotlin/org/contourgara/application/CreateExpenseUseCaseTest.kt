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
import org.contourgara.domain.Payer
import org.contourgara.domain.application.CreateExpenseUseCase
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
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

    test("支出作成メソッドが、支出保存メソッドと ID 取得メソッドとイベント保存メソッドを呼び、支出 ID とイベント ID を返す") {
        // setup
        val expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))
        val expenseEventId = ExpenseEventId(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"))

        val expense = Expense(
            expenseId = expenseId,
            amount = 1000,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            memo = "test",
        )

        val expenseEvent = ExpenseEvent(
            expenseEventID = expenseEventId,
            expenseId = expenseId,
            eventCategory = EventCategory.CREATE,
        )

        val expenseRepository = mockk<ExpenseRepository>()
        every { expenseRepository.create(expense) } returns expenseId

        val ulidClient = mockk<UlidClient>()
        every { ulidClient.nextUlid() } returns expenseEventId

        val expenseEventRepository = mockk<ExpenseEventRepository>()
        every { expenseEventRepository.save(expenseEvent) } returns Unit

        val sut = CreateExpenseUseCase(
            expenseRepository = expenseRepository,
            ulidClient = ulidClient,
            expenseEventRepository = expenseEventRepository,
        )

        // execute
        val actual = sut.execute(expense)

        // assert
        val expected = Pair(
            first = expenseId,
            second = expenseEventId,
        )
        actual shouldBe expected

        verify(exactly = 1) { expenseRepository.create(expense) }
        verify(exactly = 1) { ulidClient.nextUlid() }
        verify(exactly = 1) { expenseEventRepository.save(expenseEvent) }
    }
})
