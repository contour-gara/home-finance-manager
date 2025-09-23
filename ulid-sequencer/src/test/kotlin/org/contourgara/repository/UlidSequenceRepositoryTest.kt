package org.contourgara.repository

import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.configuration.DBUnitConfig
import com.github.database.rider.core.configuration.DataSetConfig
import com.github.database.rider.core.dsl.RiderDSL
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import java.sql.DriverManager

class UlidSequenceRepositoryTest : FunSpec({
    val mysql = MySQLContainer("mysql:8.0.43-bookworm").apply {
        startupAttempts = 1
    }

    beforeSpec {
        mysql.start()
        println("before")
        withEnvironment(mapOf(
            "ULID_SEQUENCER_DATASOURCE_URL" to mysql.jdbcUrl,
            "DATASOURCE_USERNAME" to mysql.username,
            "DATASOURCE_PASSWORD" to mysql.password,
        )) {
            migration()
        }
    }

    test("マイグレーション確認") {
        transaction {
            // execute
            val actual = UlidSequence.selectAll().toList()

            // assert
            assertSoftly {
                actual shouldHaveSize 1
                actual.first()[UlidSequence.ulid] shouldBe "01K4MXEKC0PMTJ8FA055N4SH78"
            }
        }
    }

    test("Database Rider 確認") {
        // setup
        RiderDSL.withConnection(
            DriverManager.getConnection(mysql.jdbcUrl, mysql.username, mysql.password)
        )
        RiderDSL.DataSetConfigDSL
            .withDataSetConfig(DataSetConfig("ulid.yaml"))
        RiderDSL.DBUnitConfigDSL
            .withDBUnitConfig(
                DBUnitConfig().caseInsensitiveStrategy(Orthography.LOWERCASE)
            )
            .createDataSet()

        transaction {
            // execute
            val actual = UlidSequence.select(UlidSequence.ulid).single()[UlidSequence.ulid]

            // assert
            actual shouldBe "01K4MXEKC0PMTJ8FA055N4SH79"
        }
    }
})
