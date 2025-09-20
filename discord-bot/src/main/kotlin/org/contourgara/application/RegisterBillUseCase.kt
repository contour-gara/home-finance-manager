package org.contourgara.application

import org.contourgara.domain.Bill
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single

@Single
class RegisterBillUseCase(private val ulidGenerator: UlidGenerator) {
    fun execute(param: RegisterBillParam): RegisterBillDto =
        Bill.of(ulidGenerator.generate(), param.amount, param.lender, param.claimant, param.memo).let {
            println("execute UseCase")
            RegisterBillDto.from(it)
        }
}
