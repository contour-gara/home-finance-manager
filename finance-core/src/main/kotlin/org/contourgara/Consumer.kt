package org.contourgara

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class Consumer(
    private val testComponent: TestComponent,
) {
    @KafkaListener(topics = ["\${spring.kafka.template.default-topic}"])
    fun listen(record: ConsumerRecord<String, String>) {
//        println("Received headers: ${record.headers()}")
//        println("billOperation: ${String(record.headers().headers("billOperation").first().value())}")
//        println("billId: ${String(record.headers().headers("billId").first().value())}")
        println("Received message: ${record.value()}")

        testComponent.execute()
    }
}
