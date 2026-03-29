package org.contourgara.application

import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ulid.ULID

fun nextUlid(findLatestUlid: () -> ULID, saveUlid: (ULID) -> Unit): ULID =
    transaction {
        ULID.Monotonic
            .nextULID(findLatestUlid())
            .also { saveUlid(it) }
    }
