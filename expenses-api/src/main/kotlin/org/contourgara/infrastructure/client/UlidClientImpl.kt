package org.contourgara.infrastructure.client

import org.contourgara.domain.UlidClient
import ulid.ULID

class UlidClientImpl : UlidClient {
    override fun nextUlid(): ULID = ULID.Companion.nextULID()
}
