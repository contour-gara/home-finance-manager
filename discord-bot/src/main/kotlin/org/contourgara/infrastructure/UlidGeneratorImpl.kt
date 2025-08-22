package org.contourgara.infrastructure

import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single
import ulid.ULID

@Single
class UlidGeneratorImpl : UlidGenerator {
    override fun generate(): ULID = ULID.nextULID()
}
