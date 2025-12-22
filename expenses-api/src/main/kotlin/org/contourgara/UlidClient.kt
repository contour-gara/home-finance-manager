package org.contourgara

import ulid.ULID

interface UlidClient {
    fun nextUlid(): ULID
}
