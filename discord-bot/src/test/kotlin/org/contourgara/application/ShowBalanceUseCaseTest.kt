package org.contourgara.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.mockk.coVerify
import io.mockk.mockkClass
import org.contourgara.DiscordBotModule
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.User
import org.contourgara.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

class ShowBalanceUseCaseTest : KoinTest, FunSpec() {
    init {
        extensions(
            KoinExtension(DiscordBotModule().module()) {
                mockkClass(it, relaxed = true)
            }
        )

        test("残高参照トピックにレコードを送信する") {
            // setup
            val eventSendClient = declareMock<EventSendClient> {}
            val sut: ShowBalanceUseCase by inject()
            val param = ShowBalanceParam("gara", "yuki")

            // execute
            sut.execute(param)

            // assert
            coVerify(exactly = 1) {
                eventSendClient.showBalance(
                    User.GARA,
                    User.YUKI,
                )
            }
        }
    }
}
