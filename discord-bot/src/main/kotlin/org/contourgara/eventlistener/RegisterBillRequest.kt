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
    val amount: Int,
    val lender: User = User.UNDEFINED,
    val borrower: User = User.UNDEFINED,
    val memo: String = "",
) {
    companion object {
        fun of(amount: String, lender: Long, borrower: Long, memo: String): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillRequest> =
            either {
                accumulate {
                    validateAmount(amount).bindNelOrAccumulate()
                }
                of(
                    amount = amount.toInt(),
                    lender = User.of(lender),
                    borrower = User.of(borrower),
                    memo = memo,
                ).bind()
            }

        private fun of(amount: Int, lender: User, borrower: User, memo: String): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillRequest> =
            either {
                accumulate {
                    validateLender(lender).bindNelOrAccumulate()
                    validateBorrower(borrower).bindNelOrAccumulate()
                    validateLenderAndBorrower(lender, borrower).bindNelOrAccumulate()
                    validateMemo(memo).bindNelOrAccumulate()
                }
                RegisterBillRequest(
                    amount = amount,
                    lender = lender,
                    borrower = borrower,
                    memo = memo,
                )
            }
    }

    fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(255, 255, 50)
        field(name = "請求金額", inline = true, value = { "${amount.toString().reversed().chunked(3).joinToString(",").reversed()} 円" })
        if (lender != User.UNDEFINED) field(name = "請求者", inline = true, value = { lender.name.lowercase() })
        if (borrower != User.UNDEFINED) field(name = "請求先", inline = true, value = { borrower.name.lowercase() })
        if (!memo.isEmpty()) field(name = "メモ", inline = true, value = { memo })
    }

    fun toParam(): RegisterBillParam = RegisterBillParam(amount, lender.name.lowercase(), borrower.name.lowercase(), memo)
}
