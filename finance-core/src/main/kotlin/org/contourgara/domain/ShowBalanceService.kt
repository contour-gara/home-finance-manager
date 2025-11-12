package org.contourgara.domain

object ShowBalanceService {
    fun execute(
        lendBalance: Balance,
        borrowBalance: Balance,
    ): OffsetBalance {
        require(lendBalance.lender == borrowBalance.borrower) { "Lender of lendBalance must be the same as borrower of borrowBalance" }
        require(lendBalance.borrower == borrowBalance.lender) { "Borrower of lendBalance must be the same as lender of borrowBalance" }
        return if (lendBalance.amount > borrowBalance.amount)
            Loan(
                lender = lendBalance.lender,
                borrower = lendBalance.borrower,
                amount = lendBalance.amount - borrowBalance.amount,
                lastEventId = if (lendBalance.balanceId > borrowBalance.balanceId) lendBalance.lastEventId else borrowBalance.lastEventId,
            )
        else
            Debt(
                lender = lendBalance.lender,
                borrower = lendBalance.borrower,
                amount = borrowBalance.amount - lendBalance.amount,
                lastEventId = if (lendBalance.balanceId > borrowBalance.balanceId) lendBalance.lastEventId else borrowBalance.lastEventId,
            )
    }
}
