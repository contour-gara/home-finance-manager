package org.contourgara

import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.contourgara.domain.Category
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseRepository
import org.contourgara.domain.Payer
import ulid.ULID

class CreateExpenseUseCaseTest : FunSpec({
    test("支出作成メソッドが、支出保存メソッドを呼ぶ") {
        // setup
        val expense = Expense(
            id = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"),
            amount = 1000,
            payer = Payer.DIRECT_DEBIT,
            category = Category.RENT,
            memo = "test",
        )

        val expenseRepository = mockk<ExpenseRepository>()
        every { expenseRepository.create(expense) } returns Unit

        val sut = CreateExpenseUseCase(
            expenseRepository = expenseRepository,
        )

        // execute
        sut.execute(expense)

        // assert
        verify(exactly = 1) { expenseRepository.create(expense) }
    }
})
