package org.contourgara.application

import org.contourgara.domain.EventCategory
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.contourgara.domain.infrastructure.UlidClient
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val ulidClient: UlidClient,
    private val expenseEventRepository: ExpenseEventRepository,
    private val expensesRepository: ExpensesRepository,
) {
    fun execute(param: CreateExpenseParam): CreateExpenseDto =
        transaction {
            param
                .toModel().let {
                    Pair(
                        first = expenseRepository.create(it),
                        second = ulidClient.nextUlid(),
                    )
                }.also { (expense, expenseEventID) ->
                    expenseEventRepository.save(
                        ExpenseEvent(
                            expenseEventID = expenseEventID,
                            expenseId = expense.expenseId,
                            eventCategory = EventCategory.CREATE,
                        )
                    )
                }.also {
                    val expense = param.toModel()
                    expensesRepository.findLatestExpenses(expense.year, expense.month, expense.payer, expense.category)
                }.let { (expense, expenseEventID) ->
                    CreateExpenseDto.of(expense.expenseId, expenseEventID)
                }
        }
}
