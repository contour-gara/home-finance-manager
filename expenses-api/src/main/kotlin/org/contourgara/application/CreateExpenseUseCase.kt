package org.contourgara.application

import org.contourgara.domain.EventCategory
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.Expenses
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.contourgara.domain.infrastructure.ExpenseEventIdClient
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val expenseEventIdClient: ExpenseEventIdClient,
    private val expenseEventRepository: ExpenseEventRepository,
    private val expensesRepository: ExpensesRepository,
) {
    fun execute(param: CreateExpenseParam): CreateExpenseDto =
        transaction {
            param
                .toModel()
                .let {
                    Pair(
                        first = expenseRepository.create(it),
                        second = expenseEventRepository.save(
                            ExpenseEvent(
                                expenseEventId = expenseEventIdClient.nextExpensesEventId(),
                                expenseId = it.expenseId,
                                eventCategory = EventCategory.CREATE,
                            ),
                        ),
                    )
                }.also { (expense, expenseEvent) ->
                    findAndUpdateExpenses(expense = expense, expenseEvent = expenseEvent)
                }.let { (expense, expenseEvent) ->
                    CreateExpenseDto.of(
                        expenseId = expense.expenseId,
                        expenseEventId = expenseEvent.expenseEventId,
                    )
                }
        }

    private fun findAndUpdateExpenses(expense: Expense, expenseEvent: ExpenseEvent): Expenses =
        expensesRepository.findLatestExpenses(
            year = expense.year,
            month = expense.month,
            payer = expense.payer,
            category = expense.category,
        ).let {
            Expenses
                .from(
                    expenses = it,
                    expense = expense,
                    expenseEventId = expenseEvent.expenseEventId,
                )
        }.let { expensesRepository.save(expenses = it) }
}
