package org.contourgara.application

import org.koin.core.annotation.Single

@Single
class RegisterBillUseCase {
    fun execute(param: RegisterBillParam): RegisterBillDto {
        println("execute UseCase")
        return RegisterBillDto("ID", param.amount, param.claimant, param.memo)
    }
}
