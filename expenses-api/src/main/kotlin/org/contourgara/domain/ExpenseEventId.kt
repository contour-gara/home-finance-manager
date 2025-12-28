package org.contourgara.domain

import ulid.ULID

@JvmInline
value class ExpenseEventId(val id: ULID) : Comparable<ExpenseEventId> {
    override fun compareTo(other: ExpenseEventId): Int = id.compareTo(other.id)
}
