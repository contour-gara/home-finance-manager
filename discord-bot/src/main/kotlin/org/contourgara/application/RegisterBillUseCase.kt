package org.contourgara.application

import org.contourgara.domain.Bill
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single

@Single
class RegisterBillUseCase(private val ulidGenerator: UlidGenerator) {
    fun execute(param: RegisterBillParam): RegisterBillDto =
        ulidGenerator.nextUlid()
            .let {
                Bill.of(
                    id = it,
                    amount = param.amount,
                    lenderName = param.lender,
                    borrowerName = param.borrower,
                    memo = param.memo,
                )
            }
            .let { RegisterBillDto.from(it) }
            .also { println("execute UseCase") }
}
