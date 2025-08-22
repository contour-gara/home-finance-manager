package org.contourgara.domain

import ulid.ULID

data class Bill(
    val id: ULID,
    val amount: Int,
    val claimant: User,
    val memo: String
) {
    init {
        require(amount in 1..Int.MAX_VALUE) { "請求金額は [0..Int.MAX_VALUE] でないとならない: $amount" }
        require(memo.isNotBlank()) { "メモは空文字ではならない" }
    }
}
