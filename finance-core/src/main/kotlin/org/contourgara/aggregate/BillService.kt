package org.contourgara.aggregate

import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.annotation.MessageIdentifier
import org.contourgara.domain.BalanceRepository
import org.contourgara.domain.DiscordClient
import org.contourgara.domain.RegisterBill
import org.contourgara.domain.UlidGenerator
import org.springframework.stereotype.Service

@Service
class BillService(
    private val ulidGenerator: UlidGenerator,
    private val balanceRepository: BalanceRepository,
    private val discordClient: DiscordClient,
) {
    @EventHandler
    fun handle(registerBillEvent: RegisterBillEvent, @MessageIdentifier id: String) {
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
            .also {
                discordClient.notifyRegisterBill(
                    RegisterBill(
                        billId = registerBillEvent.bill.billId.value,
                        eventId = id,
                        lender = registerBillEvent.bill.lender,
                        borrower = registerBillEvent.bill.borrower,
                    )
                )
            }
    }

    @EventHandler
    fun handle(deleteBillEvent: DeleteBillEvent, @MessageIdentifier id: String) {
        println(deleteBillEvent)
        println(id)
    }
}
