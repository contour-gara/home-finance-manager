package org.contourgara.generator

import ulid.ULID

fun generateNextUlid(previous: ULID): ULID =
    ULID
        .Monotonic
        .nextULIDStrict(previous = previous)
        ?: throw UlidOverflowException()

class UlidOverflowException(override val message: String? = "The random part of ULID overflows.") : RuntimeException(message)
