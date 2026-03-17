package org.contourgara.application

import dev.kord.common.entity.Snowflake
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.contourgara.domain.EventSendClient
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

class DeleteExpenseUseCaseTest : KoinTest, FunSpec() {
    init {
        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) {
                mockkClass(type = it)
            }
        )

        test(name = "支出を削除できる") {
            // setup
            declareMock<EventSendClient> {
                every { deleteExpense(messageId = Snowflake(value = 0)) } returns Unit
            }

            val sut: DeleteExpenseUseCase by inject()

            // execute
            val actual = sut.execute(messageId = Snowflake(value = 0))

            // assert
            val expected = Snowflake(value = 0)
            actual shouldBe expected
        }
    }
}
