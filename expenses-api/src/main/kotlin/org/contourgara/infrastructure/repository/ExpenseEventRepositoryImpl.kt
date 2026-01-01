package org.contourgara.infrastructure.repository

import arrow.core.Either
import org.contourgara.domain.Error
import org.contourgara.domain.EventCategory
import org.contourgara.domain.ExpenseEvent
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.ExpenseNotFoundError
import org.contourgara.domain.infrastructure.ExpenseEventRepository
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import ulid.ULID

object ExpenseEventRepositoryImpl : ExpenseEventRepository {
    override fun save(expenseEvent: ExpenseEvent): ExpenseEvent =
        expenseEvent
            .also {
                ExpenseEventIdTable
                    .insert {
                        it[expenseEventId] = expenseEvent.expenseEventId.value.toString()
                    }
                ExpenseEventTable
                    .insert {
                        it[expenseEventId] = expenseEvent.expenseEventId.value.toString()
                        it[expenseId] = expenseEvent.expenseId.value.toString()
                    }
                ExpenseEventCategoryTable
                    .insert {
                        it[expenseEventId] = expenseEvent.expenseEventId.value.toString()
                        it[eventCategory] = expenseEvent.eventCategory.name
                    }
            }

    override fun findByExpenseId(expenseId: ExpenseId): Either<Error, ExpenseEvent> =
        ExpenseEventIdTable
            .innerJoin(otherTable = ExpenseEventTable, onColumn = { ExpenseEventIdTable.expenseEventId }, otherColumn = { ExpenseEventTable.expenseEventId })
            .innerJoin(otherTable = ExpenseEventCategoryTable, onColumn = { ExpenseEventIdTable.expenseEventId }, otherColumn = { ExpenseEventCategoryTable.expenseEventId })
            .select(
                ExpenseEventIdTable.expenseEventId,
                ExpenseEventTable.expenseId,
                ExpenseEventCategoryTable.eventCategory,
            )
            .where { ExpenseEventTable.expenseId eq expenseId.value.toString() }
            .orderBy(column = ExpenseEventIdTable.expenseEventId, order = SortOrder.DESC)
            .limit(count = 1)
            .singleOrNull()
            ?.let {
                Either.Right(
                    value = ExpenseEvent(
                        expenseEventId = ExpenseEventId(value = ULID.parseULID(it[ExpenseEventIdTable.expenseEventId])),
                        expenseId = ExpenseId(value = it[ExpenseEventTable.expenseId]),
                        eventCategory = EventCategory.valueOf(value = it[ExpenseEventCategoryTable.eventCategory]),
                    ),
                )
            } ?: Either.Left(
                value = ExpenseNotFoundError(
                    expenseId = expenseId.value.toString(),
                ),
            )
}

private object ExpenseEventTable : Table("expense_event") {
    val expenseEventId = varchar("expense_event_id", 26)
    val expenseId = varchar("expense_id", 26)
}

private object ExpenseEventCategoryTable : Table("expense_event_category") {
    val expenseEventId = varchar("expense_event_id", 26)
    val eventCategory = varchar("event_category", 100)
}
