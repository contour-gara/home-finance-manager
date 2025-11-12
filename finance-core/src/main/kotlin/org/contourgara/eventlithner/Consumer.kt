package org.contourgara.eventlithner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.contourgara.application.DeleteBillUseCase
import org.contourgara.application.ShowBalanceUseCase
import org.contourgara.application.RegisterBillUseCase
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class Consumer(
    private val registerBillUseCase: RegisterBillUseCase,
    private val deleteBillUseCase: DeleteBillUseCase,
    private val showBalanceUseCase: ShowBalanceUseCase,
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

    @KafkaListener(topics = ["\${application.balance.topic.show}"])
    fun listenShowBalanceTopic(record: ConsumerRecord<String, String>) {
        showBalanceUseCase.execute(objectMapper.readValue(record.value(), ShowBalanceRequest::class.java).toParam())
    }
}
