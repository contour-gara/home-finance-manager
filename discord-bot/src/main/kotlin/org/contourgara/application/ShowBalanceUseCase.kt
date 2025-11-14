package org.contourgara.application

import org.contourgara.domain.EventSendClient
import org.koin.core.annotation.Single

@Single
class ShowBalanceUseCase(
    private val eventSendClient: EventSendClient,
) {
    fun execute(param: ShowBalanceParam) {
        param
            .toModel()
            .also { eventSendClient.showBalance(it.first, it.second) }
    }
}
