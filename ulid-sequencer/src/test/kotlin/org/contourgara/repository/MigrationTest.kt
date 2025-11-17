package org.contourgara.repository

import com.github.database.rider.core.api.configuration.Orthography
import com.github.database.rider.core.configuration.DBUnitConfig
import com.github.database.rider.core.configuration.DataSetConfig
import com.github.database.rider.core.dsl.RiderDSL
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.withEnvironment
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.MySQLContainer
import java.sql.DriverManager

@Order(value = 0)
class MigrationTest : FunSpec({
    val mysql = MySQLContainer("mysql:8.0.43-oraclelinux9").apply {
        startupAttempts = 1
        withReuse(true)
        withLabel("test.module", "ulid-sequencer")
    }

    beforeSpec {
        mysql.start()
        RiderDSL.withConnection(
            DriverManager.getConnection(mysql.jdbcUrl, mysql.username, mysql.password)
        )
        RiderDSL.DBUnitConfigDSL
            .withDBUnitConfig(
                DBUnitConfig().caseInsensitiveStrategy(Orthography.LOWERCASE)
            )

        // clean up database
        Database.connect(
            url = mysql.jdbcUrl,
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql.username,
            password = mysql.password,
        )
        transaction {
            SchemaUtils.drop(UlidSequence)
        }
    }

    test("マイグレーション確認") {
        withEnvironment(
            environment = mapOf(
                "ULID_SEQUENCER_DATASOURCE_URL" to mysql.jdbcUrl,
                "DATASOURCE_USERNAME" to mysql.username,
                "DATASOURCE_PASSWORD" to mysql.password,
            ),
            mode = OverrideMode.SetOrOverride,
        ) {
            // execute
            migration()

            // assert
            RiderDSL.DataSetConfigDSL
                .withDataSetConfig(DataSetConfig("ulid-init.yaml"))
            RiderDSL.DBUnitConfigDSL.expectDataSet()
        }
    }
})
