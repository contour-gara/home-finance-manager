package org.contourgara.infrastructure

import org.contourgara.domain.Balance
import org.contourgara.domain.User
import ulid.ULID

data class BalanceEntity(
    val balanceId: String,
    val lender: String,
    val borrower: String,
    val amount: Int,
    val lastEventId: String,
) {
    fun toModel(): Balance = Balance(
        balanceId = ULID.parseULID(balanceId),
        lender = User.valueOf(lender),
        borrower = User.valueOf(borrower),
        amount = amount,
        lastEventId = lastEventId,
    )
}

fun Balance.toEntity(): BalanceEntity = BalanceEntity(
    balanceId = this.balanceId.toString(),
    lender = this.lender.name,
    borrower = this.borrower.name,
    amount = this.amount,
    lastEventId = this.lastEventId,
)
