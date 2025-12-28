package org.contourgara.infrastructure.repository

import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.insert

object ExpenseEventRepositoryImpl : ExpenseEventRepository {
    override fun save(expenseEvent: ExpenseEvent): ExpenseEvent =
        expenseEvent
            .also {
                ExpenseEventIdTable
                    .insert {
                        it[expenseEventId] = expenseEvent.expenseEventID.id.toString()
                    }
                ExpenseEventTable
                    .insert {
                        it[expenseEventId] = expenseEvent.expenseEventID.id.toString()
                        it[expenseId] = expenseEvent.expenseId.id.toString()
                    }
                ExpenseEventCategoryTable
                    .insert {
                        it[expenseEventId] = expenseEvent.expenseEventID.id.toString()
                        it[eventCategory] = expenseEvent.eventCategory.name
                    }
            }
}

private object ExpenseEventTable : Table("expense_event") {
    val expenseEventId = varchar("expense_event_id", 26)
    val expenseId = varchar("expense_id", 26)
}

private object ExpenseEventCategoryTable : Table("expense_event_category") {
    val expenseEventId = varchar("expense_event_id", 26)
    val eventCategory = varchar("event_category", 100)
}
