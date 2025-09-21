package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import org.contourgara.application.RegisterBillParam
import org.contourgara.eventlistener.RegisterBillValidation.validateAmount
import org.contourgara.eventlistener.RegisterBillValidation.validateLender
import org.contourgara.eventlistener.RegisterBillValidation.validateMemo
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError
import org.contourgara.eventlistener.RegisterBillValidation.validateBorrower
import org.contourgara.eventlistener.RegisterBillValidation.validateLenderAndBorrower

@OptIn(ExperimentalRaiseAccumulateApi::class)
@ConsistentCopyVisibility
data class RegisterBillRequest private constructor(
    private val amount: Int,
    private val lender: User = User.UNDEFINED,
    private val borrower: User = User.UNDEFINED,
    private val memo: String = "",
) {
    companion object {
        fun of(amount: String, lender: Long, borrower: Long, memo: String): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillRequest> =
            of(
                amount = amount,
                lender = User.of(lender),
                borrower = User.of(borrower),
                memo = memo,
            )

        private fun of(amount: String, lender: User, borrower: User, memo: String): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillRequest> =
            either {
                accumulate {
                    validateAmount(amount).bindNelOrAccumulate()
                    validateLender(lender).bindNelOrAccumulate()
                    validateBorrower(borrower).bindNelOrAccumulate()
                    validateLenderAndBorrower(lender, borrower).bindNelOrAccumulate()
                    validateMemo(memo).bindNelOrAccumulate()
                }
                RegisterBillRequest(
                    amount = amount.toInt(),
                    lender = lender,
                    borrower = borrower,
                    memo = memo,
                )
            }
    }

    fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(255, 255, 50)
        field(name = "請求金額", inline = true, value = { amount.formatAmount() })
        field(name = "請求者", inline = true, value = { lender.name.lowercase() })
        field(name = "請求先", inline = true, value = { borrower.name.lowercase() })
        field(name = "メモ", inline = true, value = { memo })
    }

    fun toParam(): RegisterBillParam = RegisterBillParam(amount, lender.name.lowercase(), borrower.name.lowercase(), memo)
}
