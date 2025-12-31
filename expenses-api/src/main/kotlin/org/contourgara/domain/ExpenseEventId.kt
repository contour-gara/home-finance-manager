package org.contourgara.domain

import ulid.ULID

@JvmInline
value class ExpenseEventId(val value: ULID) : Comparable<ExpenseEventId> {
    override fun compareTo(other: ExpenseEventId): Int = value.compareTo(other.value)
}
