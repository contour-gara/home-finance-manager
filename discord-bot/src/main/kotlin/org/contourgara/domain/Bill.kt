package org.contourgara.domain

import ulid.ULID

@ConsistentCopyVisibility
data class Bill private constructor(
    val id: ULID,
    val amount: Int,
    val claimant: User,
    val memo: String
) {
    init {
        require(amount in 1..Int.MAX_VALUE) { "請求金額は [0..Int.MAX_VALUE] でないとならない: $amount" }
        require(memo.isNotBlank()) { "メモは空文字ではならない" }
    }

    companion object {
        fun of(id: ULID, amount: Int, userName: String, memo: String) =
            Bill(id, amount, User.of(userName), memo)
    }
}
