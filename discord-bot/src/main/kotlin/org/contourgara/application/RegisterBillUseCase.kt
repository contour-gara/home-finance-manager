package org.contourgara.application

import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single

@Single
class RegisterBillUseCase(private val ulidGenerator: UlidGenerator) {
    fun execute(param: RegisterBillParam): RegisterBillDto {
        println("execute UseCase")
        return RegisterBillDto(ulidGenerator.generate().toString(), param.amount, param.claimant, param.memo)
    }
}
