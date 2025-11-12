package org.contourgara.application

import org.contourgara.domain.Bill
import org.contourgara.domain.BillId
import org.contourgara.domain.User
import ulid.ULID

data class RegisterBillParam(
    val billId: ULID,
    val amount: Int,
    val lender: UserParam,
    val borrower: UserParam,
    val memo: String,
) {
    fun toModel(): Bill = Bill.of(
        billId = billId,
        amount = amount,
        lender = lender.toModel(),
        borrower = borrower.toModel(),
        memo = memo,
    )
}

data class DeleteBillParam(
    val billId: ULID,
) {
    fun toModel(): BillId = BillId(billId)
}

data class ShowBalanceParam(
    val lender: UserParam,
    val borrower: UserParam,
) {
    fun reverse() = ShowBalanceParam(
        lender = borrower,
        borrower = lender,
    )
}

enum class UserParam {
    GARA,
    YUKI,
    ;
    fun toModel(): User = User.valueOf(name)
}
