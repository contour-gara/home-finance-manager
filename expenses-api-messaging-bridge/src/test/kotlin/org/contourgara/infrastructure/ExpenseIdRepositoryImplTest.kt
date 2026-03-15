package org.contourgara.infrastructure

import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup_kotlin.dbSetup
import dev.kord.common.entity.Snowflake
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.TestContainerSpecExtension
import io.kotest.matchers.shouldBe
import org.assertj.db.api.Assertions.assertThat
import org.assertj.db.type.AssertDbConnection
import org.assertj.db.type.AssertDbConnectionFactory
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.MessageId
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.mysql.MySQLContainer
import ulid.ULID

class ExpenseIdRepositoryImplTest : FunSpec({
    lateinit var assertDbConnection: AssertDbConnection

    val mysql = install(ext = TestContainerSpecExtension(container = MySQLContainer("mysql:8.0.43-oraclelinux9")))
        .apply { startupAttempts = 1 }

    val sut = ExpenseIdRepositoryImpl

    beforeSpec {
        DbTestHelper.migrateAndConnect(mysql)
        assertDbConnection = AssertDbConnectionFactory.of(mysql.jdbcUrl, mysql.username, mysql.password).create()
    }

    beforeTest {
        DbTestHelper.deleteAllData(mysql.jdbcUrl, mysql.username, mysql.password)
    }

    test("支出 ID 保存メソッドが、支出 ID、メッセージ ID を保存する") {
        // setup
        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
        ) {
            insertInto("processed_message_id") {
                columns("message_id")
                values("1478034413110427842")
            }
        }
            .launch()

        val expenseIdTable = assertDbConnection.table("expense_id").build()

        val expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K4MXEKC0PMTJ8FA055N4SH79"))
        val messageId = MessageId(value = Snowflake(value = 1478034413110427842))

        // execute
        transaction { sut.save(expenseId = expenseId, messageId = messageId) }

        // assert
        assertThat(expenseIdTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("message_id").isEqualTo("1478034413110427842")
    }

    test("メッセージ ID に紐づいた支出 ID がある場合、支出 ID が返す") {
        // setup
        dbSetup(
            to = DriverManagerDestination(mysql.jdbcUrl, mysql.username, mysql.password),
        ) {
            insertInto("processed_message_id") {
                columns("message_id")
                values("1478034413110427842")
            }
            insertInto("expense_id") {
                columns("expense_id", "message_id")
                values("01K4MXEKC0PMTJ8FA055N4SH79", "1478034413110427842")
            }
        }
            .launch()

        val expenseIdTable = assertDbConnection.table("expense_id").build()

        val messageId = MessageId(value = Snowflake(value = 1478034413110427842))

        // execute
        val actual = transaction { sut.findByMessageId(messageId = messageId) }

        // assert
        val expected = ExpenseId(value = ULID.parseULID(ulidString = "01K4MXEKC0PMTJ8FA055N4SH79"))
        actual shouldBe expected

        assertThat(expenseIdTable)
            .hasNumberOfRows(1)
            .row(0)
            .value("expense_id").isEqualTo("01K4MXEKC0PMTJ8FA055N4SH79")
            .value("message_id").isEqualTo("1478034413110427842")
    }

    test("メッセージ ID に紐づいた支出 ID がない場合、例外を投げる") {
        // setup
        val messageId = MessageId(value = Snowflake(value = 1478034413110427842))

        // execute & assert
        shouldThrowAny { transaction { sut.findByMessageId(messageId = messageId) } }
    }
})
