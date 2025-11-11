package org.contourgara.application

import org.axonframework.queryhandling.QueryHandler
import org.contourgara.domain.Balance
import org.contourgara.domain.BalanceRepository
import org.springframework.stereotype.Service

@Service
class BalanceQuery(
    private val balanceRepository: BalanceRepository
) {
    @QueryHandler
    fun handle(param: OffsetBalanceParam): Balance =
        balanceRepository
            .findLatest(
                lender = param.lender.toModel(),
                borrower = param.borrower.toModel(),
            )
}
