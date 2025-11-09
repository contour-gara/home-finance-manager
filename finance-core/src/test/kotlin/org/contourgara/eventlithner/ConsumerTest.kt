package org.contourgara.eventlithner

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.Bill
import org.contourgara.BillId
import org.contourgara.DeleteBillCommand
import org.contourgara.RegisterBillCommand
import org.contourgara.TestcontainersConfiguration
import org.contourgara.User
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean
import ulid.ULID
import java.time.Duration

@ApplyExtension(SpringExtension::class)
@SpringBootTest
@Import(TestcontainersConfiguration::class)
class ConsumerTest(
    val kafkaTemplate: KafkaTemplate<String, String>,
    @MockitoBean val commandGateway: CommandGateway,
) : FunSpec({
    test("登録イベントのテスト") {
        kafkaTemplate.send("register-bill", "{\"billId\": \"01K9HSSXN6VYPGG5E10Q1TFAGF\", \"amount\": 1234, \"lender\": \"GARA\", \"borrower\": \"YUKI\", \"memo\": \"asdf\"}")

        await withPollDelay (Duration.ofSeconds(1)) atMost (Duration.ofSeconds(10)) untilAsserted {
            Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(
                RegisterBillCommand(
                    Bill.Companion.of(
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

    test("削除イベントのテスト") {
        kafkaTemplate.send("delete-bill", "{\"billId\": \"01K9HSSXN6VYPGG5E10Q1TFAGF\"}")

        await withPollDelay(Duration.ofSeconds(1))  atMost(Duration.ofSeconds(10)) untilAsserted {
            Mockito.verify(commandGateway, Mockito.times(1)).sendAndWait(
                DeleteBillCommand(
                    billId = BillId(ULID.parseULID("01K9HSSXN6VYPGG5E10Q1TFAGF")),
                )
            )
        }
    }
})
