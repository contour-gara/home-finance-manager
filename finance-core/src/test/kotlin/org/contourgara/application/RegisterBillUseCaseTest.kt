package org.contourgara.application

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.Bill
import org.contourgara.RegisterBillCommand
import org.contourgara.TestcontainersConfiguration
import org.contourgara.User
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import ulid.ULID
import java.time.Duration

@ApplyExtension(SpringExtension::class)
@SpringBootTest
@Import(TestcontainersConfiguration::class)
class RegisterBillUseCaseTest(
    val sut : RegisterBillUseCase,
    @MockitoBean val commandGateway: CommandGateway,
) : FunSpec({
    test("test") {
        // setup
        val param = RegisterBillParam(
            billId = ULID.parseULID("01K9HSSXN6VYPGG5E10Q1TFAGF"),
            amount = 1234,
            lender = UserParam.GARA,
            borrower = UserParam.YUKI,
            memo = "asdf",
        )

        // execute
        sut.execute(param)

        // assert
        await withPollDelay (Duration.ofSeconds(1)) atMost (Duration.ofSeconds(10)) untilAsserted {
            Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(
                RegisterBillCommand(
                    Bill.of(
                        billId = ULID.parseULID("01K9HSSXN6VYPGG5E10Q1TFAGF"),
                        amount = 1234,
                        lender = User.GARA,
                        borrower = User.YUKI,
                        memo = "asdf",
                    )
                )
            )
        }
    }
})
