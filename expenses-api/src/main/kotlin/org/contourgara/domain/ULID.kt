package org.contourgara.domain

import arrow.core.EitherNel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import ulid.ULID

@OptIn(ExperimentalRaiseAccumulateApi::class)
fun ULID.Companion.of(ulidString: String): EitherNel<Error, ULID> =
    either {
        accumulate {
            ensureOrAccumulate(
                condition = runCatching { ULID.parseULID(ulidString = ulidString) }.isSuccess,
            ) {
                ValidationError(
                    pointer = "/ulid",
                    invalidValue = ulidString,
                    detail = "value is not a valid ULID format.",
                )
            }
        }
        ULID.parseULID(ulidString = ulidString)
    }
