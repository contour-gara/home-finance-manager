package org.contourgara.eventlithner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.Bill
import org.contourgara.BillId
import org.contourgara.DeleteBillCommand
import org.contourgara.RegisterBillCommand
import org.contourgara.User
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ulid.ULID

@Component
class Consumer(
    private val commandGateway: CommandGateway,
) {
    @KafkaListener(topics = ["\${application.bill.topic.register}"])
    fun listenRegisterTopic(record: ConsumerRecord<String, String>) {
        val value = ObjectMapper().registerKotlinModule().readValue(record.value(), RegisterBill::class.java)

        commandGateway.sendAndWait<RegisterBillCommand>(
            RegisterBillCommand(
                Bill.of(
                    billId = ULID.parseULID(value.billId),
                    amount = value.amount,
                    lender = User.valueOf(value.lender.name),
                    borrower = User.valueOf(value.borrower.name),
                    memo = value.memo,
                )
            )
        )
    }

    @KafkaListener(topics = ["\${application.bill.topic.delete}"])
    fun listenDeleteTopic(record: ConsumerRecord<String, String>) {
        val value = ObjectMapper().registerKotlinModule().readValue(record.value(), DeleteBill::class.java)

        commandGateway.sendAndWait<DeleteBillCommand>(
            DeleteBillCommand(
                billId = BillId(ULID.parseULID(value.billId)),
            )
        )
    }
}
