package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.either
import ulid.ULID

@JvmInline
value class ExpenseId(val id: ULID) {
    companion object {
//        fun of(id: String): EitherNel<String, ExpenseId> =
//            either {
//                ULID.of(ulidString = id).bind()
//                ExpenseId(id)
//            }
    }

    constructor(id: String) : this(id = ULID.parseULID(ulidString = id))
}
