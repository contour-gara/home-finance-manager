package org.contourgara.domain

import ulid.ULID

interface UlidClient {
    fun nextUlid(): ULID
}
