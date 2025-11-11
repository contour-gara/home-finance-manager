package org.contourgara.domain

sealed interface OffsetBalance {
    val lender: User
    val borrower: User
    val amount: Int
    val lastEventId: String
}

data class Loan(
    override val lender: User,
    override val borrower: User,
    override val amount: Int,
    override val lastEventId: String,
) : OffsetBalance

data class Debt(
    override val lender: User,
    override val borrower: User,
    override val amount: Int,
    override val lastEventId: String,
) : OffsetBalance
