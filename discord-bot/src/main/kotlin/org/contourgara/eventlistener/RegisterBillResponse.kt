package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.EmbedBuilder
import org.contourgara.application.RegisterBillDto
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError
import org.contourgara.eventlistener.RegisterBillValidation.validateAmount
import org.contourgara.eventlistener.RegisterBillValidation.validateBorrower
import org.contourgara.eventlistener.RegisterBillValidation.validateLender
import org.contourgara.eventlistener.RegisterBillValidation.validateLenderAndBorrower
import org.contourgara.eventlistener.RegisterBillValidation.validateMemo

@OptIn(ExperimentalRaiseAccumulateApi::class)
@ConsistentCopyVisibility
data class RegisterBillResponse private constructor (
    private val billId: String,
    private val amount: Int,
    private val lender: User,
    private val borrower: User,
    private val memo: String,
) {
    companion object {
        fun from(dto: RegisterBillDto): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            of(
                billId = dto.billId,
                amount = dto.amount.toString(),
                lender = User.of(dto.lender),
                borrower = User.of(dto.borrower),
                memo = dto.memo,
            )

        private fun of(billId: String, amount: String, lender: User, borrower: User, memo: String): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            either {
                accumulate {
                    validateAmount(amount).bindNelOrAccumulate()
                    validateLender(lender).bindNelOrAccumulate()
                    validateBorrower(borrower).bindNelOrAccumulate()
                    validateLenderAndBorrower(lender, borrower).bindNelOrAccumulate()
                    validateMemo(memo).bindNelOrAccumulate()
                }
                RegisterBillResponse(
                    billId = billId,
                    amount = amount.toInt(),
                    lender = lender,
                    borrower = borrower,
                    memo = memo,
                )
            }
    }

    fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(0, 255, 0)
        field(name = "請求 ID", inline = true, value = { billId })
        field(name = "請求金額", inline = true, value = { amount.formatAmount() })
        field(name = "請求者", inline = true, value = { lender.name.lowercase() })
        field(name = "請求先", inline = true, value = { borrower.name.lowercase() })
        field(name = "メモ", inline = true, value = { memo })
    }

    fun getBorrowerId(): Snowflake = Snowflake(borrower.id)
}
