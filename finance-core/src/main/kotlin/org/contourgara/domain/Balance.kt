package org.contourgara.domain

import ulid.ULID

data class Balance(
    val balanceId: ULID,
    val lender: User,
    val borrower: User,
    val amount: Int,
    val lastEventId: String,
) {
    companion object {
        fun noRecord(lender: User, borrower: User): Balance = Balance(
            balanceId = ULID.parseULID("00000000000000000000000000"),
            lender = lender,
            borrower = borrower,
            amount = 0,
            lastEventId = "",
        )
    }

    fun register(newId: ULID, deltaAmount: Int, eventId: String): Balance =
        copy(
            balanceId = newId,
            amount = amount + deltaAmount,
            lastEventId = eventId,
        )

    fun delete(newId: ULID, deltaAmount: Int, eventId: String): Balance =
        copy(
            balanceId = newId,
            amount = amount - deltaAmount,
            lastEventId = eventId,
        )
}
