package org.contourgara.application

import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.RegisterBillCommand
import org.springframework.stereotype.Service

@Service
class RegisterBillUseCase(
    private val commandGateway: CommandGateway,
) {
    fun execute(param: RegisterBillParam) {
        commandGateway.sendAndWait<RegisterBillCommand>(RegisterBillCommand(param.toModel()))
    }
}
