package org.contourgara.aggregate

import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.annotation.MessageIdentifier
import org.contourgara.domain.BalanceRepository
import org.contourgara.domain.UlidGenerator
import org.springframework.stereotype.Service

@Service
class BillService(
    private val ulidGenerator: UlidGenerator,
    private val balanceRepository: BalanceRepository,
) {
    @EventHandler
    fun handle(registerBillEvent: RegisterBillEvent, @MessageIdentifier id: String) {
        println(registerBillEvent)
        registerBillEvent
            .bill
            .let {
                balanceRepository
                    .findLatest(
                        lender = registerBillEvent.bill.lender,
                        borrower = registerBillEvent.bill.borrower,
                    )
            }
            .update(
                newId = ulidGenerator.nextUlid(),
                deltaAmount = registerBillEvent.bill.amount,
                eventId = id,
            )
            .also { balanceRepository.save(it) }
        println(id)
        // TODO: BillId ~ を、EventId ~ として処理しました的な通知を行う
    }

    @EventHandler
    fun handle(deleteBillEvent: DeleteBillEvent, @MessageIdentifier id: String) {
        println(deleteBillEvent)
        println(id)
    }
}
