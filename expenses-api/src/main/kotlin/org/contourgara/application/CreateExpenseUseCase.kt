package org.contourgara.application

import org.contourgara.domain.EventCategory
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.Expenses
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
                        second = expenseEventRepository.save(
                            ExpenseEvent(
                                expenseEventId = ulidClient.nextUlid(),
                                expenseId = it.expenseId,
                                eventCategory = EventCategory.CREATE,
                            ),
                        ),
                    )
                }.also { (expense, expenseEvent) ->
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
                }.let { (expense, expenseEvent) ->
                    CreateExpenseDto.of(expense.expenseId, expenseEvent.expenseEventId)
                }
        }
}
