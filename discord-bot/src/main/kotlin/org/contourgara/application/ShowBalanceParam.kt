package org.contourgara.application

import org.contourgara.domain.User

data class ShowBalanceParam(
    val lender: String,
    val borrower: String,
) {
    fun toModel(): Pair<User, User> =
        Pair(
            User.of(lender),
            User.of(borrower),
        )
}
