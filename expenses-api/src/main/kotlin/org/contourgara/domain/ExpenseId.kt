package org.contourgara.domain

import ulid.ULID

@JvmInline
value class ExpenseId(val id: ULID) {
    constructor(id: String) : this(id = ULID.parseULID(ulidString = id))
}
