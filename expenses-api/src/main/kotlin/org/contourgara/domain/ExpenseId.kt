package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import ulid.ULID

@JvmInline
@OptIn(ExperimentalRaiseAccumulateApi::class)
value class ExpenseId(val id: ULID) {
    companion object {
        fun of(id: String): EitherNel<Error, ExpenseId> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = runCatching { ULID.parseULID(ulidString = id) }.isSuccess,
                    ) {
                        ValidationError(
                            pointer = "expenseId",
                            invalidValue = id,
                            detail = "value is not a valid ULID format.",
                        )
                    }
                }
                ExpenseId(id = id)
            }

    }

    constructor(id: String) : this(id = ULID.parseULID(ulidString = id))
}
