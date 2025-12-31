package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either

@OptIn(ExperimentalRaiseAccumulateApi::class)
enum class Month(val value: Int) {
    JANUARY(value = 1),
    FEBRUARY(value = 2),
    MARCH(value = 3),
    APRIL(value = 4),
    MAY(value = 5),
    JUNE(value = 6),
    JULY(value = 7),
    AUGUST(value = 8),
    SEPTEMBER(value = 9),
    OCTOBER(value = 10),
    NOVEMBER(value = 11),
    DECEMBER(value = 12),
    ;

    companion object {
        fun of(value: Int): Month =
            entries.firstOrNull {
                it.value == value
            } ?: throw IllegalArgumentException("Not found: $value")

        fun ofValidate(value: Int): EitherNel<Error, Month> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = Month.entries.any {
                            it.value == value
                        },
                    ) {
                        ValidationError(
                            pointer = "month",
                            invalidValue = value,
                            detail = "value is not supported.",
                        )
                    }
                }
                Month.of(value = value)
            }
    }
}
