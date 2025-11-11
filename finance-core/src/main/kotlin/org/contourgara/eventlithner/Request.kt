package org.contourgara.eventlithner

import org.contourgara.application.DeleteBillParam
import org.contourgara.application.OffsetBalanceParam
import org.contourgara.application.RegisterBillParam
import org.contourgara.application.UserParam
import ulid.ULID

data class RegisterBillRequest(
    val billId: String,
    val amount: Int,
    val lender: UserRequest,
    val borrower: UserRequest,
    val memo: String,
) {
    fun toParam(): RegisterBillParam = RegisterBillParam(
        billId = ULID.parseULID(billId),
        amount = amount,
        lender = lender.toParam(),
        borrower = borrower.toParam(),
        memo = memo,
    )
}

data class DeleteBillRequest(
    val billId: String,
) {
    fun toParam(): DeleteBillParam = DeleteBillParam(
        billId = ULID.parseULID(billId),
    )
}

data class OffsetBalanceRequest(
    val lender: UserRequest,
    val borrower: UserRequest,
) {
    fun toParam(): OffsetBalanceParam = OffsetBalanceParam(
        lender = lender.toParam(),
        borrower = borrower.toParam(),
    )
}

enum class UserRequest {
    GARA,
    YUKI,
    ;
    fun toParam(): UserParam = UserParam.valueOf(name)
}
