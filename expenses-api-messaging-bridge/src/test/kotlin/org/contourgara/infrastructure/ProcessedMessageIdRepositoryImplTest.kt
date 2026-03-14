package org.contourgara.infrastructure

import dev.kord.common.entity.Snowflake
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerSpecExtension
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.MessageId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.mysql.MySQLContainer

class ProcessedMessageIdRepositoryImplTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val mysql = install(ext = TestContainerSpecExtension(container = MySQLContainer("mysql:8.0.43-oraclelinux9")))
        .apply { startupAttempts = 1 }

    val sut = ProcessedMessageIdRepositoryImpl

    beforeSpec {
        DbTestHelper.migrateAndConnect(mysql)
        assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl, mysql.username, mysql.password).create()
    }

    beforeTest {
        DbTestHelper.deleteAllData(mysql.jdbcUrl, mysql.username, mysql.password)
    }

    test("処理済みメッセージ ID 保存メソッドが、メッセージ ID を保存する") {
        // setup
        val processedMessageIdTable = assertDbConnection.table("processed_message_id").build()

        val messageId = MessageId(value = Snowflake(value = 1478034413110427842))

        // execute
        transaction { sut.save(messageId = messageId) }

        // assert
        assertThat(processedMessageIdTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("message_id").isEqualTo("1478034413110427842")
    }
})
