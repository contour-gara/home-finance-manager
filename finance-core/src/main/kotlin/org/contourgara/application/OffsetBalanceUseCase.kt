package org.contourgara.application

import org.axonframework.queryhandling.QueryGateway
import org.contourgara.domain.Balance
import org.contourgara.domain.Debt
import org.contourgara.domain.Loan
import org.contourgara.domain.OffsetBalanceService
import org.springframework.stereotype.Service

@Service
class OffsetBalanceUseCase(
    private val queryGateway: QueryGateway,
) {
    fun execute(param: OffsetBalanceParam) {
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
                OffsetBalanceService.execute(it.first, it.second)
            }
            .also {
                when (it) {
                    is Loan -> println("貸している通知")
                    is Debt -> println("借りている通知")
                }
            }
    }
}
