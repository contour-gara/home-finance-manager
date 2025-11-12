package org.contourgara.application

import org.contourgara.domain.EventSendClient
import org.koin.core.annotation.Single

@Single
class DeleteBillUseCase(
    private val eventSendClient: EventSendClient,
) {
    fun execute(param: DeleteBillParam) {
        param
            .toModel()
            .also { eventSendClient.deleteBill(it) }
    }
}
