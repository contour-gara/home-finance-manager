package org.contourgara.application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import ulid.ULID

suspend fun nextUlid(findLatestUlid: () -> ULID, generateNextUlid: (ULID) -> ULID, saveUlid: (ULID) -> Unit): ULID =
    withContext(context = Dispatchers.IO) {
        suspendTransaction {
            runCatching {
                generateNextUlid(findLatestUlid())
            }
                .fold(
                    onSuccess = { it },
                    onFailure = { throw ApplicationException(message = it.message, cause = it) },
                )
                .also { saveUlid(it) }
        }
    }

fun nextUlidByStateful(generateNextUlid: () -> ULID): ULID =
    runCatching {
        generateNextUlid()
    }
        .fold(
            onSuccess = { it },
            onFailure = { throw ApplicationException(message = it.message, cause = it) },
        )

class ApplicationException(override val message: String?, override val cause: Throwable?) : RuntimeException(message, cause)
