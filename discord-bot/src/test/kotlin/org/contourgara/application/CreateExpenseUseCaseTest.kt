package org.contourgara.application

import dev.kord.common.entity.Snowflake
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.datetime.LocalDate
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.Expense
import org.contourgara.domain.SystemClock
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import kotlin.getValue

class CreateExpenseUseCaseTest : KoinTest, FunSpec() {
    init {
        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) {
                mockkClass(type = it)
            }
        )

        test("支出を登録できる") {
            // setup
            val expense = Expense(
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                localDate = LocalDate(year = 2026, month = 1, day = 1),
                memo = """
                    1/1
                    test
                """.trimIndent(),
            )
            val messageId = Snowflake(value = 0)
            declareMock<EventSendClient> {
                coEvery { createExpense(messageId = messageId, expense = expense) } returns Unit
            }

            val createExpenseParam = CreateExpenseParam(
                messageId = Snowflake(value = 0),
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                localDate = LocalDate(year = 2026, month = 1, day = 1),
                memo = "test",
            )

            val sut: CreateExpenseUseCase by inject()

            // execute
            val actual = sut.execute(createExpenseParam)

            // assert
            val expected = CreateExpenseDto(
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                localDate = LocalDate(year = 2026, month = 1, day = 1),
                memo = """
                    1/1
                    test
                """.trimIndent(),
            )
            actual shouldBe expected
        }

        test("日付が指定されていない場合、現在の日時で登録する") {
            // setup
            declareMock<SystemClock> {
                every { today() } returns LocalDate(year = 2026, month = 4, day = 18)
            }

            val expense = Expense(
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                localDate = LocalDate(year = 2026, month = 4, day = 18),
                memo = """
                    4/18
                    test
                """.trimIndent(),
            )
            val messageId = Snowflake(value = 0)
            declareMock<EventSendClient> {
                coEvery { createExpense(messageId = messageId, expense = expense) } returns Unit
            }

            val createExpenseParam = CreateExpenseParam(
                messageId = Snowflake(value = 0),
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                localDate = null,
                memo = "test",
            )

            val sut: CreateExpenseUseCase by inject()

            // execute
            val actual = sut.execute(createExpenseParam)

            // assert
            val expected = CreateExpenseDto(
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                localDate = LocalDate(year = 2026, month = 4, day = 18),
                memo = """
                    4/18
                    test
                """.trimIndent(),
            )
            actual shouldBe expected
        }
    }
}
