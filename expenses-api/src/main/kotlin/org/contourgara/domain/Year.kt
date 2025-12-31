package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either

@OptIn(ExperimentalRaiseAccumulateApi::class)
enum class Year(val value: Int) {
    _2026(value = 2026),
    _2027(value = 2027),
    ;

    companion object {
        fun of(value: Int): Year =
            entries.firstOrNull {
                it.value == value
            } ?: throw IllegalArgumentException("Not found: $value")

        fun ofValidate(value: Int): EitherNel<Error, Year> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = entries.any {
                            it.value == value
                        },
                    ) {
                        ValidationError(
                            pointer = "year",
                            invalidValue = value,
                            detail = "value is not supported.",
                        )
                    }
                }
                Year.of(value = value)
            }
    }
}
