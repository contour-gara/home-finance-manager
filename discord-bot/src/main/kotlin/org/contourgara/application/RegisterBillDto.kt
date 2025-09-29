package org.contourgara.application

import org.contourgara.domain.Bill

data class RegisterBillDto(
    val billId: String,
    val amount: Int,
    val lender: String,
    val borrower: String,
    val memo: String,
) {
    companion object {
        fun from(bill: Bill): RegisterBillDto =
            RegisterBillDto(
                billId = bill.billId.toString(),
                amount = bill.amount,
                lender = bill.lender.lowercaseName(),
                borrower = bill.borrower.lowercaseName(),
                memo = bill.memo,
            )
    }
}
