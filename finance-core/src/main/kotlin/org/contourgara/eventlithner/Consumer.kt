package org.contourgara.eventlithner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.contourgara.application.DeleteBillUseCase
import org.contourgara.application.OffsetBalanceUseCase
import org.contourgara.application.RegisterBillUseCase
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class Consumer(
    private val registerBillUseCase: RegisterBillUseCase,
    private val deleteBillUseCase: DeleteBillUseCase,
    private val offsetBalanceUseCase: OffsetBalanceUseCase,
) {
    private val objectMapper: ObjectMapper by lazy { ObjectMapper().registerKotlinModule() }

    @KafkaListener(topics = ["\${application.bill.topic.register}"])
    fun listenRegisterBillTopic(record: ConsumerRecord<String, String>) {
        registerBillUseCase.execute(objectMapper.readValue(record.value(), RegisterBillRequest::class.java).toParam())
    }

    @KafkaListener(topics = ["\${application.bill.topic.delete}"])
    fun listenDeleteBillTopic(record: ConsumerRecord<String, String>) {
        deleteBillUseCase.execute(objectMapper.readValue(record.value(), DeleteBillRequest::class.java).toParam())
    }

    @KafkaListener(topics = ["\${application.balance.topic.offset}"])
    fun listenOffsetBalanceTopic(record: ConsumerRecord<String, String>) {
        offsetBalanceUseCase.execute(objectMapper.readValue(record.value(), OffsetBalanceRequest::class.java).toParam())
    }
}
