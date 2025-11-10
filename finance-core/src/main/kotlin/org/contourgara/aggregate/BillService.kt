package org.contourgara.aggregate

import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.annotation.MessageIdentifier
import org.springframework.stereotype.Service

@Service
class BillService {
    @EventHandler
    fun on(registerBillEvent: RegisterBillEvent, @MessageIdentifier id: String) {
        println(registerBillEvent)
        println(id)
    }

    @EventHandler
    fun on(deleteBillEvent: DeleteBillEvent, @MessageIdentifier id: String) {
        println(deleteBillEvent)
        println(id)
    }
}
