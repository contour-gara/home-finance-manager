package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import ulid.ULID

@JvmInline
@OptIn(ExperimentalRaiseAccumulateApi::class)
value class ExpenseId(val value: ULID) {
    companion object {
        fun of(value: String): EitherNel<Error, ExpenseId> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = runCatching { ULID.parseULID(ulidString = value) }.isSuccess,
                    ) {
                        ValidationError(
                            pointer = "expenseId",
                            invalidValue = value,
                            detail = "value is not a valid ULID format.",
                        )
                    }
                }
                ExpenseId(value = value)
            }

    }

    constructor(value: String) : this(value = ULID.parseULID(ulidString = value))
}
