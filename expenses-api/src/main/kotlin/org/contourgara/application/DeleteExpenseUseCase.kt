package org.contourgara.application

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.toEitherNel
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.infrastructure.ExpenseEventIdClient
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.contourgara.domain.toException
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ulid.ULID

class DeleteExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val expenseEventRepository: ExpenseEventRepository,
    private val expensesRepository: ExpensesRepository,
    private val expenseEventIdClient: ExpenseEventIdClient,
) {
    fun execute(expenseId: String): ULID =
        transaction {
            ExpenseId
                .of(value = expenseId)
                .flatMap {
                    expenseEventRepository
                        .findByExpenseId(expenseId = it)
                        .toEitherNel()
                }
                .flatMap {
                    it
                        .delete(
                            deleteEventId = expenseEventIdClient.nextExpensesEventId(),
                        )
                        .toEitherNel()
                }
                .map {
                    expenseEventRepository.save(expenseEvent = it)
                }
                .onRight { (expenseEventId, expenseId, eventCategory) ->
                    expenseRepository
                        .findById(expenseId = expenseId)
                        ?. let { expense ->
                            expensesRepository.findLatestExpenses(
                                year = expense.year,
                                month = expense.month,
                                payer = expense.payer,
                                category = expense.category,
                            )
                                ?.deleteExpense(expense = expense, deleteEventId = expenseEventId)
                        }
                        ?. let { expenses ->
                            expensesRepository.save(expenses = expenses)
                        }
                }
                .let {
                    when (it) {
                        is Either.Left -> throw it.value.toException()
                        is Either.Right -> it.value.expenseEventId.value
                    }
                }
        }
}
