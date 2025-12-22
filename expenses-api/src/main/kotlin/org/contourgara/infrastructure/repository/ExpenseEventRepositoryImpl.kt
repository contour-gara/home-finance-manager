package org.contourgara.infrastructure.repository

import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.jetbrains.exposed.v1.jdbc.insert

object ExpenseEventRepositoryImpl : ExpenseEventRepository {
    override fun save(expenseEvent: ExpenseEvent) =
        Unit
            .also {
                ExpenseEventIdTable
                    .insert {
                        it[expenseEventId] = expenseEvent.expenseEventID.id.toString()
                    }
            }
}
