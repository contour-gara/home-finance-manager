package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either

@JvmInline
@OptIn(ExperimentalRaiseAccumulateApi::class)
value class Memo(val value: String) {
    companion object {
        fun of(value: String): EitherNel<Error, Memo> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = value.isNotBlank(),
                    ) {
                        ValidationError(
                            pointer = "memo",
                            invalidValue = value,
                            detail = "memo must not be blank.",
                        )
                    }
                }
                Memo(value = value)
            }
    }
}
