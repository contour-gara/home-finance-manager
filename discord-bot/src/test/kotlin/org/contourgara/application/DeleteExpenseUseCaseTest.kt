package org.contourgara.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import ulid.ULID
import kotlin.getValue

class DeleteExpenseUseCaseTest : KoinTest, FunSpec() {
    init {
        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) {
                mockkClass(type = it)
            }
        )

        test(name = "支出を削除できる") {
            // setup
            val expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW"))
            declareMock<ExpenseClient> {
                every {
                    delete(expenseId = expenseId)
                } returns Pair(
                    first = expenseId,
                    second = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
                )
            }

            val sut: DeleteExpenseUseCase by inject()

            // execute
            val actual = sut.execute(expenseId = "01K5EZVS4SQ695EMPX61GM7KHW")

            // assert
            val expected = Pair(
                first = "01K5EZVS4SQ695EMPX61GM7KHW",
                second = "01KD27JEZQQY88RG18034YZHBV",
            )
            actual shouldBe expected
        }
    }
}
