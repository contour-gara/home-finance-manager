package org.contourgara.repository

import org.jetbrains.exposed.v1.core.Table

object UlidSequence : Table("ulid_sequence") {
    val ulid = varchar("ulid", 26)
}
