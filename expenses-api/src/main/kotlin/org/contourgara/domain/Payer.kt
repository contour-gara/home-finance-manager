package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either

@OptIn(ExperimentalRaiseAccumulateApi::class)
enum class Payer {
    GARA,
    YUKI,
    DIRECT_DEBIT,
    ;

    companion object {
        fun of(value: String): EitherNel<Error, Payer> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = entries.any { it.name == value },
                    ) {
                        ValidationError(
                            pointer = "payer",
                            invalidValue = value,
                            detail = "value is not supported.",
                        )
                    }
                }
                Payer.valueOf(value = value)
            }
    }
}
