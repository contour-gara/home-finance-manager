package org.contourgara.repository

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ulid.ULID

object UlidSequenceRepository {
    fun findLatestUlid(): ULID = transaction {
        UlidSequence.select(UlidSequence.ulid)
            .orderBy(UlidSequence.ulid to SortOrder.DESC)
            .limit(1)
            .single()
            .let { it[UlidSequence.ulid] }
            .let { ULID.parseULID(it) }
    }

    fun insert(ulid: ULID) {
        transaction {
            UlidSequence.insert { it[UlidSequence.ulid] = ulid.toString() }
        }
    }
}
