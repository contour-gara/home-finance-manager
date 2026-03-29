package org.contourgara.application

import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ulid.ULID

fun nextUlid(findLatestUlid: () -> ULID, generateNextUlid: (ULID) -> ULID, saveUlid: (ULID) -> Unit): ULID =
    transaction {
        generateNextUlid(findLatestUlid())
            .also { saveUlid(it) }
    }
