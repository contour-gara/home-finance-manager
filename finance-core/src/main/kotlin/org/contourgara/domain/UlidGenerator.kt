package org.contourgara.domain

import ulid.ULID

fun interface UlidGenerator {
    fun nextUlid(): ULID
}
