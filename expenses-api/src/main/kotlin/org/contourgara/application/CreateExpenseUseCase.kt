package org.contourgara.application

import org.contourgara.domain.EventCategory
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.contourgara.domain.infrastructure.ExpenseRepository
import org.contourgara.domain.infrastructure.UlidClient
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CreateExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val ulidClient: UlidClient,
    private val expenseEventRepository: ExpenseEventRepository,
) {
    fun execute(param: CreateExpenseParam): CreateExpenseDto =
        transaction {
            param
                .toModel()
                .let {
                    Pair(
                        first = expenseRepository.create(it),
                        second = ulidClient.nextUlid(),
                    )
                }
                .also { (expenseId, expenseEventID) ->
                    expenseEventRepository.save(
                        ExpenseEvent(
                            expenseEventID = expenseEventID,
                            expenseId = expenseId,
                            eventCategory = EventCategory.CREATE,
                        )
                    )
                }
                .let { (expenseId, expenseEventID) ->
                    CreateExpenseDto.of(expenseId, expenseEventID)
                }
        }
}
