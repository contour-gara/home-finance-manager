package org.contourgara.application

import ulid.ULID

fun nextUlid(latestUlid: () -> ULID, saveUlid: (ULID) -> Unit): ULID =
    ULID.Monotonic
        .nextULID(latestUlid())
        .also { saveUlid(it) }
