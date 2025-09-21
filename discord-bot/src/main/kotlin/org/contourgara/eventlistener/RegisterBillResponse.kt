package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.EmbedData
import dev.kord.rest.builder.message.EmbedBuilder
import org.contourgara.application.RegisterBillDto
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError
import org.contourgara.eventlistener.RegisterBillValidation.validateAmount
import org.contourgara.eventlistener.RegisterBillValidation.validateBorrower
import org.contourgara.eventlistener.RegisterBillValidation.validateLender
import org.contourgara.eventlistener.RegisterBillValidation.validateEmbedData
import org.contourgara.eventlistener.RegisterBillValidation.validateLenderAndBorrower
import org.contourgara.eventlistener.RegisterBillValidation.validateMemo
import kotlin.collections.first
import kotlin.collections.last

@OptIn(ExperimentalRaiseAccumulateApi::class)
@ConsistentCopyVisibility
data class RegisterBillResponse private constructor (
    private val id: String,
    private val amount: Int,
    private val lender: User,
    private val borrower: User,
    private val memo: String,
) {
    companion object {
        fun from(dto: RegisterBillDto): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            of(
                id = dto.id,
                amount = dto.amount.toString(),
                lender = User.of(dto.lender),
                borrower = User.of(dto.borrower),
                memo = dto.memo,
            )

        fun from(embedData: EmbedData): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            either {
                accumulate {
                    validateEmbedData(embedData).bindNelOrAccumulate()
                }
                of(
                    id = embedData.fields.orEmpty().first().value,
                    amount = embedData.fields.orEmpty()[1].value.split(" ").first().replace(",", ""),
                    lender = User.of(embedData.fields.orEmpty()[2].value),
                    borrower = User.of(embedData.fields.orEmpty()[3].value),
                    memo = embedData.fields.orEmpty().last().value
                ).bind()
            }

        private fun of(id: String, amount: String, lender: User, borrower: User, memo: String): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            either {
                accumulate {
                    validateAmount(amount).bindNelOrAccumulate()
                    validateLender(lender).bindNelOrAccumulate()
                    validateBorrower(borrower).bindNelOrAccumulate()
                    validateLenderAndBorrower(lender, borrower).bindNelOrAccumulate()
                    validateMemo(memo).bindNelOrAccumulate()
                }
                RegisterBillResponse(
                    id = id,
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
        field(name = "申請 ID", inline = true, value = { id })
        field(name = "請求金額", inline = true, value = { "${amount.toString().reversed().chunked(3).joinToString(",").reversed()} 円" })
        field(name = "請求者", inline = true, value = { lender.name.lowercase() })
        field(name = "請求先", inline = true, value = { borrower.name.lowercase() })
        field(name = "メモ", inline = true, value = { memo })
    }

    fun getBorrowerId(): Snowflake = Snowflake(borrower.id)
}
