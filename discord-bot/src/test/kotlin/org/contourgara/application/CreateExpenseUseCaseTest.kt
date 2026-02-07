package org.contourgara.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.UlidGenerator
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import ulid.ULID
import kotlin.getValue

class CreateExpenseUseCaseTest : KoinTest, FunSpec() {
    init {
        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) {
                mockkClass(type = it)
            }
        )

        test(name = "ULID を生成し、支出を登録できる") {
            // setup
            declareMock<UlidGenerator> {
                every { nextUlid() } returns ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW")
            }

            val expense = Expense(
                expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW")),
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                year = 2026,
                month = 1,
                memo = "test",
            )
            declareMock<ExpenseClient> {
                every {
                    create(
                        expense = expense
                    )
                } returns Pair(
                    first = expense,
                    second = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV"))
                )
            }

            val createExpenseParam = CreateExpenseParam(
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                year = 2026,
                month = 1,
                memo = "test",
            )

            val sut: CreateExpenseUseCase by inject()

            // execute
            val actual = sut.execute(createExpenseParam)

            // assert
            val expected = CreateExpenseDto(
                expenseId = "01K5EZVS4SQ695EMPX61GM7KHW",
                expenseEventId = "01KD27JEZQQY88RG18034YZHBV",
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                year = 2026,
                month = 1,
                memo = "test",
            )
            actual shouldBe expected
        }
    }
}
