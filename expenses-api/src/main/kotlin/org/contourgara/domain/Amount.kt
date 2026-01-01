package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either

@JvmInline
@OptIn(ExperimentalRaiseAccumulateApi::class)
value class Amount(val value: Int) {
    companion object {
        fun of(value: Int): EitherNel<Error, Amount> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = value >= 0,
                    ) {
                        ValidationError(
                            pointer = "amount",
                            invalidValue = value,
                            detail = "amount must be positive.",
                        )
                    }
                }
                Amount(value = value)
            }
    }
    operator fun plus(other: Amount): Amount = Amount(value + other.value)
}
