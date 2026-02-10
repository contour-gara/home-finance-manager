package org.contourgara.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.mysql.MySQLContainer

class MigrationTest : FunSpec({
    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
    }

    beforeSpec {
        mysql.start()
        withEnvironment(
            environment = mapOf(
                "ULID_SEQUENCER_DATASOURCE_URL" to mysql.jdbcUrl,
                "DATASOURCE_USERNAME" to mysql.username,
                "DATASOURCE_PASSWORD" to mysql.password,
            ),
            mode = OverrideMode.SetOrOverride,
        ) {
            migration()
        }
    }

    test("マイグレーション確認") {
        transaction {
            // execute
            val actual = UlidSequence.selectAll()
                .toList()
                .map { it[UlidSequence.ulid] }

            // assert
            val expected = listOf("01K4MXEKC0PMTJ8FA055N4SH78")
            actual shouldBe expected
        }
    }
})
