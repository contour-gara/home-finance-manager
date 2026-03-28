package org.contourgara.application

import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ulid.ULID

fun nextUlid(latestUlid: () -> ULID, saveUlid: (ULID) -> Unit): ULID =
    transaction {
        ULID.Monotonic
            .nextULID(latestUlid())
            .also { saveUlid(it) }
    }
