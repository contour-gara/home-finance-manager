package org.contourgara

import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ulid.ULID

@Component
class Consumer(
    private val testComponent: TestComponent,
    private val commandGateway: CommandGateway,
) {
    @KafkaListener(topics = ["\${spring.kafka.template.default-topic}"])
    fun listen(record: ConsumerRecord<String, String>) {
//        println("Received headers: ${record.headers()}")
        val billOperation = String(record.headers().headers("billOperation").first().value())
        println("billOperation: $billOperation")
        val billId = String(record.headers().headers("billId").first().value())
        println("billId: $billId")
//        println("Received message: ${record.value()}")

        val value = Json.decodeFromString<RegisterBill>(record.value())
        println(value)

        testComponent.execute()

        try {
            when (billOperation) {
                "REGISTER" -> commandGateway.sendAndWait<RegisterBillCommand>(
                    RegisterBillCommand(
                        Bill.of(
                            billId = value.billId,
                            amount = value.amount,
                            lender = value.lender,
                            borrower = value.borrower,
                            memo = value.memo,
                        )
                    )
                )
                "DELETE" -> commandGateway.sendAndWait<DeleteBillCommand>(
                    DeleteBillCommand(
                        billId = BillId(ULID.parseULID(billId)),
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
