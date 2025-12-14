package org.contourgara.application

import org.axonframework.commandhandling.gateway.CommandGateway
import org.contourgara.aggregate.DeleteBillCommand
import org.contourgara.domain.BillId
import org.springframework.stereotype.Service

@Service
class DeleteBillUseCase(
    private val commandGateway: CommandGateway,
) {
    fun execute(param: DeleteBillParam) {
        commandGateway.sendAndWait<DeleteBillCommand>(DeleteBillCommand(BillId(param.billId), param.registerNotificationId))
    }
}
