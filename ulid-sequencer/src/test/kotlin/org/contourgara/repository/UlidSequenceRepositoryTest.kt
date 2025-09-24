package org.contourgara.repository

import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.configuration.DBUnitConfig
import com.github.database.rider.core.configuration.DataSetConfig
import com.github.database.rider.core.dsl.RiderDSL
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.MySQLContainer
import ulid.ULID
import java.sql.DriverManager

class UlidSequenceRepositoryTest : FunSpec({
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

    context("最新の ULID を取得できる") {
        data class TestCase(val setupData: String, val expected: ULID) : WithDataTestName {
            override fun dataTestName(): String = "初期状態が $setupData の場合、$expected が返る"
        }

        withData(
            TestCase("ulid1.yaml", ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")),
            TestCase("ulid3.yaml", ULID.parseULID("01K5WW47HCRZYKEX8MQN5PKDA8")),
        ) { (setupData, expected) ->
            // setup
            RiderDSL.withConnection(
                DriverManager.getConnection(mysql.jdbcUrl, mysql.username, mysql.password)
            )
            RiderDSL.DataSetConfigDSL
                .withDataSetConfig(DataSetConfig(setupData))
            RiderDSL.DBUnitConfigDSL
                .withDBUnitConfig(
                    DBUnitConfig().caseInsensitiveStrategy(Orthography.LOWERCASE)
                )
                .createDataSet()

            // execute
            val actual = UlidSequenceRepository.findLatestUlid()

            // assert
            actual shouldBe expected

            RiderDSL.DataSetConfigDSL
                .withDataSetConfig(DataSetConfig(setupData))
            RiderDSL.DBUnitConfigDSL.expectDataSet()
        }
    }

    context("ULID を挿入できる") {
        data class TestCase(val setupData: String, val expectedData: String, val ulid: ULID) : WithDataTestName {
            override fun dataTestName(): String = "初期状態が $setupData で $ulid を挿入した場合、テーブルが $expectedData の状態になる"
        }

        withData(
            TestCase("ulid1.yaml", "ulid2.yaml", ULID.parseULID("01K5WW47H8XQWXDGW7458533JF")),
            TestCase("ulid2.yaml", "ulid3.yaml", ULID.parseULID("01K5WW47HCRZYKEX8MQN5PKDA8")),
        ) { (setupData, expectedData, ulid) ->
            // setup
            RiderDSL.withConnection(
                DriverManager.getConnection(mysql.jdbcUrl, mysql.username, mysql.password)
            )
            RiderDSL.DataSetConfigDSL
                .withDataSetConfig(DataSetConfig(setupData))
            RiderDSL.DBUnitConfigDSL
                .withDBUnitConfig(
                    DBUnitConfig().caseInsensitiveStrategy(Orthography.LOWERCASE)
                )
                .createDataSet()

            // execute
            UlidSequenceRepository.insert(ulid)

            // assert
            RiderDSL.DataSetConfigDSL
                .withDataSetConfig(DataSetConfig(expectedData))
            RiderDSL.DBUnitConfigDSL.expectDataSet()
        }
    }
})
