package org.contourgara.eventlithner

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.contourgara.TestcontainersConfiguration
import org.contourgara.application.DeleteBillParam
import org.contourgara.application.DeleteBillUseCase
import org.contourgara.application.RegisterBillParam
import org.contourgara.application.RegisterBillUseCase
import org.contourgara.application.UserParam
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
    @MockitoBean val registerBillUseCase: RegisterBillUseCase,
    @MockitoBean val deleteBillUseCase: DeleteBillUseCase,
) : FunSpec({
    test("登録イベントのテスト") {
        // setup
        kafkaTemplate.send("register-bill", "{\"billId\": \"01K9HSSXN6VYPGG5E10Q1TFAGF\", \"amount\": 1234, \"lender\": \"GARA\", \"borrower\": \"YUKI\", \"memo\": \"asdf\"}")

        // execute & assert
        await withPollDelay (Duration.ofSeconds(1)) atMost (Duration.ofSeconds(10)) untilAsserted {
            Mockito.verify(registerBillUseCase, Mockito.times(1)).execute(
                RegisterBillParam(
                    billId = ULID.parseULID("01K9HSSXN6VYPGG5E10Q1TFAGF"),
                    amount = 1234,
                    lender = UserParam.GARA,
                    borrower = UserParam.YUKI,
                    memo = "asdf",
                )
            )
        }
    }

    test("削除イベントのテスト") {
        // setup
        kafkaTemplate.send("delete-bill", "{\"billId\": \"01K9HSSXN6VYPGG5E10Q1TFAGF\"}")

        // execute & assert
        await withPollDelay(Duration.ofSeconds(1))  atMost(Duration.ofSeconds(10)) untilAsserted {
            Mockito.verify(deleteBillUseCase, Mockito.times(1)).execute(
                DeleteBillParam(
                    billId = ULID.parseULID("01K9HSSXN6VYPGG5E10Q1TFAGF"),
                )
            )
        }
    }
})
