package org.contourgara.application

import org.contourgara.domain.EventSendClient
import org.koin.core.annotation.Single

@Single
class DeleteBillUseCase(
    private val eventSendClient: EventSendClient,
) {
    fun execute(param: DeleteBillParam): DeleteBillDto =
        param
            .toModel()
            .also { eventSendClient.deleteBill(it) }
            .let {
                DeleteBillDto(
                    billId = it.value,
                    registerBillMessageId = param.registerBillMessageId,
                )
            }
}
