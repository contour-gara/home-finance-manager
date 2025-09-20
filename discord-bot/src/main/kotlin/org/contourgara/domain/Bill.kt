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
        require(amount in 1..Int.MAX_VALUE) { "請求金額は [0..Int.MAX_VALUE] でないとならない: $amount" }
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
