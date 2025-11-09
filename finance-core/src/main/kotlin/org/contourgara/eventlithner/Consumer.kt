package org.contourgara.eventlithner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.application.DeleteBillUseCase
import org.contourgara.application.RegisterBillUseCase
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class Consumer(
    private val registerBillUseCase: RegisterBillUseCase,
    private val deleteBillUseCase: DeleteBillUseCase,
) {
    @KafkaListener(topics = ["\${application.bill.topic.register}"])
    fun listenRegisterTopic(record: ConsumerRecord<String, String>) {
        val registerBillRequest = ObjectMapper().registerKotlinModule().readValue(record.value(), RegisterBillRequest::class.java)

        registerBillUseCase.execute(registerBillRequest.toParam())
    }

    @KafkaListener(topics = ["\${application.bill.topic.delete}"])
    fun listenDeleteTopic(record: ConsumerRecord<String, String>) {
        val deleteBillRequest = ObjectMapper().registerKotlinModule().readValue(record.value(), DeleteBillRequest::class.java)

        deleteBillUseCase.execute(deleteBillRequest.toParam())
    }
}
