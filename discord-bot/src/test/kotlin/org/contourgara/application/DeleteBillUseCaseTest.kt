package org.contourgara.application

import dev.kord.common.entity.Snowflake
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.mockk.mockkClass
import io.mockk.verify
import org.contourgara.domain.BillId
import org.contourgara.domain.EventSendClient
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import ulid.ULID
import kotlin.getValue

class DeleteBillUseCaseTest :  KoinTest, FunSpec() {
    init {
        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) {
                mockkClass(it, relaxed = true)
            }
        )

        test("削除トピックにレコードを送信する") {
            // setup
            val ulid = ULID.parseULID("01K5C11Z3TPPZ5H95MMTQV77RP")

            val eventSendClient = declareMock<EventSendClient> {}

            val sut: DeleteBillUseCase by inject()

            val param = DeleteBillParam(ulid, Snowflake(123456789012345678))

            // execute
            sut.execute(param)

            // assert
            verify(exactly = 1) {
                eventSendClient.deleteBill(
                    BillId(ulid)
                )
            }
        }
    }
}
