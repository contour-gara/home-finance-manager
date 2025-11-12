package org.contourgara.application

import org.axonframework.queryhandling.QueryGateway
import org.contourgara.domain.Balance
import org.contourgara.domain.Debt
import org.contourgara.domain.DiscordClient
import org.contourgara.domain.Loan
import org.contourgara.domain.ShowBalanceService
import org.springframework.stereotype.Service

@Service
class ShowBalanceUseCase(
    private val queryGateway: QueryGateway,
    private val discordClient: DiscordClient,
) {
    fun execute(param: ShowBalanceParam) {
        queryGateway.query(param, Balance::class.java)
            .thenCombine(queryGateway.query(param.reverse(), Balance::class.java)) {
                result1, result2 ->
                Pair(
                    first = result1,
                    second = result2,
                )
            }
            .get()
            .let {
                ShowBalanceService.execute(it.first, it.second)
            }
            .also {
                when (it) {
                    is Loan -> discordClient.notifyOffsetBalance(it)
                    is Debt -> discordClient.notifyOffsetBalance(it)
                }
            }
    }
}
