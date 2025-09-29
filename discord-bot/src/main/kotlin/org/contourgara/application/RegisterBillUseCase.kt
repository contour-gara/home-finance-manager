package org.contourgara.application

import org.contourgara.domain.Bill
import org.contourgara.domain.BillOperation
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single

@Single
class RegisterBillUseCase(
    private val ulidGenerator: UlidGenerator,
    private val eventSendClient: EventSendClient,
) {
    fun execute(param: RegisterBillParam): RegisterBillDto =
        ulidGenerator.nextUlid()
            .let {
                Bill.of(
                    billId = it,
                    amount = param.amount,
                    lenderName = param.lender,
                    borrowerName = param.borrower,
                    memo = param.memo,
                )
            }
            .also { eventSendClient.execute(BillOperation.REGISTER, it) }
            .let { RegisterBillDto.from(it) }
}
