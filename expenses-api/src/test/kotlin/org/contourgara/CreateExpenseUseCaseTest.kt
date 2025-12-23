package org.contourgara

import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.contourgara.domain.Category
import org.contourgara.domain.EventCategory
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventID
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.Payer
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

    test("支出作成メソッドが、支出保存メソッドと ID 取得メソッドとイベント保存メソッドを呼ぶ") {
        // setup
        val expense = Expense(
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            amount = 1000,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            memo = "test",
        )

        val expenseEvent = ExpenseEvent(
            expenseEventID = ExpenseEventID(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV")),
            expenseId = ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            eventCategory = EventCategory.CREATE,
        )

        val expenseRepository = mockk<ExpenseRepository>()
        every { expenseRepository.create(expense) } returns ExpenseId(ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))

        val ulidClient = mockk<UlidClient>()
        every { ulidClient.nextUlid() } returns ExpenseEventID(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"))

        val expenseEventRepository = mockk<ExpenseEventRepository>()
        every { expenseEventRepository.save(expenseEvent) } returns ExpenseEventID(ULID.parseULID("01KD27JEZQQY88RG18034YZHBV"))

        val sut = CreateExpenseUseCase(
            expenseRepository = expenseRepository,
            ulidClient = ulidClient,
            expenseEventRepository = expenseEventRepository,
        )

        // execute
        sut.execute(expense)

        // assert
        verify(exactly = 1) { expenseRepository.create(expense) }
        verify(exactly = 1) { ulidClient.nextUlid() }
        verify(exactly = 1) { expenseEventRepository.save(expenseEvent) }
    }
})
