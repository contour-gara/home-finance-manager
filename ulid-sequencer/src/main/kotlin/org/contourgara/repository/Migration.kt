package org.contourgara.repository

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun migration() {
    Database.connect(
        url = System.getenv("ULID_SEQUENCER_DATASOURCE_URL"),
        driver = "com.mysql.cj.jdbc.Driver",
        user = System.getenv("DATASOURCE_USERNAME"),
        password = System.getenv("DATASOURCE_PASSWORD"),
    )

    transaction {
        if (SchemaUtils.listTables().isEmpty()) {
            SchemaUtils.create(UlidSequence)
            UlidSequence.insert { it[ulid] = "01K4MXEKC0PMTJ8FA055N4SH78" }
        }
    }
}
