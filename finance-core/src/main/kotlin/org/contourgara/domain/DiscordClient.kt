package org.contourgara.domain

interface DiscordClient {
    fun notifyRegisterBill(registerBill: RegisterBill)
    fun notifyDeleteBill(deleteBill: DeleteBill)
    fun notifyOffsetBalance(loan: Loan)
    fun notifyOffsetBalance(debt: Debt)
}
