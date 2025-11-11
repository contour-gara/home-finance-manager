package org.contourgara.aggregate

import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.annotation.MessageIdentifier
import org.contourgara.domain.BalanceRepository
import org.contourgara.domain.DeleteBill
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
        balanceRepository
            .findLatest(
                lender = registerBillEvent.bill.lender,
                borrower = registerBillEvent.bill.borrower,
                )
            .register(
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
        balanceRepository
            .findLatest(
                lender = deleteBillEvent.bill.lender,
                borrower = deleteBillEvent.bill.borrower,
            )
            .delete(
                newId = ulidGenerator.nextUlid(),
                deltaAmount = deleteBillEvent.bill.amount,
                eventId = id,
            )
            .also { balanceRepository.save(it) }
            .also {
                discordClient.notifyDeleteBill(
                    DeleteBill(
                        billId = deleteBillEvent.bill.billId.value,
                        eventId = id,
                        lender = deleteBillEvent.bill.lender,
                        borrower = deleteBillEvent.bill.borrower,
                    )
                )
            }
    }
}
