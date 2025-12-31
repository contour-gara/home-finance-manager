package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either

@OptIn(ExperimentalRaiseAccumulateApi::class)
enum class Category {
    RENT,
    UTILITIES,
    FOOD,
    DAILY_NEEDS,
    HEALTHCARE,
    ENTERTAINMENT,
    TRANSPORTATION,
    TRAVEL,
    OTHER
    ;

    companion object {
        fun of(value: String): EitherNel<Error, Category> =
            either {
                accumulate {
                    ensureOrAccumulate(
                        condition = entries.any { it.name == value },
                    ) {
                        ValidationError(
                            pointer = "category",
                            invalidValue = value,
                            detail = "value is not supported.",
                        )
                    }
                }
                valueOf(value = value)
            }
    }
}
