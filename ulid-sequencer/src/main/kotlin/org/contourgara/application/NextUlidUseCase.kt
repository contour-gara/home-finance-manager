package org.contourgara.application

import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ulid.ULID

fun nextUlid(findLatestUlid: () -> ULID, generateNextUlid: (ULID) -> ULID, saveUlid: (ULID) -> Unit): ULID =
    transaction {
        runCatching {
            generateNextUlid(findLatestUlid())
        }
            .fold(
                onSuccess = { it },
                onFailure = { throw ApplicationException(message = it.message, cause = it) },
            )
            .also { saveUlid(it) }
    }

class ApplicationException(override val message: String?, override val cause: Throwable?) : RuntimeException(message, cause)
