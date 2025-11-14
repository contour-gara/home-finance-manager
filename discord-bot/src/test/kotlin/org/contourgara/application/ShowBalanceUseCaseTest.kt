package org.contourgara.application

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.mockk.mockkClass
import io.mockk.verify
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.User
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

class ShowBalanceUseCaseTest : KoinTest, FunSpec() {
    init {
        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) {
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
            verify(exactly = 1) {
                eventSendClient.showBalance(
                    User.GARA,
                    User.YUKI,
                )
            }
        }
    }
}
