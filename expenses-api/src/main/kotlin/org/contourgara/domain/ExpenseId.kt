package org.contourgara.domain

import arrow.core.EitherNel
import ulid.ULID

@JvmInline
value class ExpenseId(val id: ULID) {
    companion object {
        fun of(id: String): EitherNel<Error, ExpenseId> =
            ULID
                .of(ulidString = id)
                .map { ExpenseId(id = it) }
    }

    constructor(id: String) : this(id = ULID.parseULID(ulidString = id))
}
