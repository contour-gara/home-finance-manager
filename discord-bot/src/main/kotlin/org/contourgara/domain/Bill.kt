package org.contourgara.domain

import ulid.ULID

@ConsistentCopyVisibility
data class Bill private constructor(
    val id: ULID,
    val amount: Int,
    val lender: User,
    val borrower: User,
    val memo: String,
) {
    init {
        require(amount >= 1) { "請求金額は 1 円以上でないとならない: $amount" }
        require(lender != borrower) { "請求者と請求先は同じではならない" }
        require(memo.isNotBlank()) { "メモは空文字ではならない" }
    }

    companion object {
        fun of(id: ULID, amount: Int, lenderName: String, borrowerName: String, memo: String) =
            Bill(
                id = id,
                amount = amount,
                lender = User.of(lenderName),
                borrower = User.of(borrowerName),
                memo = memo,
            )
    }
}
