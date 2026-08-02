package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@JvmInline
@OptIn(ExperimentalRaiseAccumulateApi::class)
value class ValidatedDate(val value: LocalDate) {
    companion object {
        fun of(value: LocalDate): EitherNel<Error, ValidatedDate> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = Year
                            .entries
                            .map { it.value }
                            .contains(element = value.year),
                    ) {
                        ValidationError(
                            pointer = "validatedDate",
                            invalidValue = value.toString(),
                            detail = "value is not supported.",
                        )
                    }
                }
                ValidatedDate(value = value)
            }
    }

    val year: Year get() = Year.of(value = value.year)
    val month: Month get() = Month.of(value = value.month.number)
}
