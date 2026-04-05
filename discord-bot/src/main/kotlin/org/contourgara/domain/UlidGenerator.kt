package org.contourgara.domain

import ulid.ULID

fun interface UlidGenerator {
    suspend fun nextUlid(): ULID
}
