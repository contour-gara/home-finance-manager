package org.contourgara.application

import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.DeleteBillCommand
import org.springframework.stereotype.Service

@Service
class DeleteBillUseCase(
    private val commandGateway: CommandGateway,
) {
    fun execute(param: DeleteBillParam) {
        commandGateway.sendAndWait<DeleteBillCommand>(DeleteBillCommand(param.toModel()))
    }
}
