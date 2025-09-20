package org.contourgara.application

import org.contourgara.domain.Bill

data class RegisterBillDto(
    val id: String,
    val amount: Int,
    val lender: String,
    val claimant: String,
    val memo: String,
) {
    companion object {
        fun from(bill: Bill): RegisterBillDto =
            RegisterBillDto(bill.id.toString(), bill.amount, bill.lender.lowercaseName(), bill.claimant.lowercaseName(), bill.memo)
    }
}
