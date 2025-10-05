package org.contourgara

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.awaitility.kotlin.withPollDelay
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration

@ApplyExtension(SpringExtension::class)
@SpringBootTest
@Import(TestcontainersConfiguration::class)
class ConsumerTest(
    val kafkaTemplate: KafkaTemplate<String, String>,
    @MockitoBean val testComponent: TestComponent,
) : FunSpec({
    test("test") {
        kafkaTemplate.send("home-finance-manager-topic", "test")
        await withPollDelay(Duration.ofSeconds(1))  atMost(Duration.ofSeconds(10)) untilAsserted {
            Mockito.verify(testComponent, Mockito.times(1)).execute()
        }
    }
})
