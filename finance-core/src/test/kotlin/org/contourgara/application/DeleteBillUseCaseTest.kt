package org.contourgara.application

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.BillId
import org.contourgara.DeleteBillCommand
import org.contourgara.TestcontainersConfiguration
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import ulid.ULID
import java.time.Duration

@ApplyExtension(SpringExtension::class)
@SpringBootTest
@Import(TestcontainersConfiguration::class)
class DeleteBillUseCaseTest(
    val sut : DeleteBillUseCase,
    @MockitoBean val commandGateway: CommandGateway,
) : FunSpec({
    test("test") {
        // setup
        val param = DeleteBillParam(
            billId = ULID.parseULID("01K9HSSXN6VYPGG5E10Q1TFAGF"),
        )

        // execute
        sut.execute(param)

        // assert
        await withPollDelay(Duration.ofSeconds(1)) atMost(Duration.ofSeconds(10)) untilAsserted {
            Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(
                DeleteBillCommand(
                    billId = BillId(ULID.parseULID("01K9HSSXN6VYPGG5E10Q1TFAGF")),
                )
            )
        }
    }
})
