package org.contourgara.generator

import ulid.ULID

fun generateNextUlid(previous: ULID): ULID =
    ULID
        .Monotonic
        .nextULID(previous = previous)
