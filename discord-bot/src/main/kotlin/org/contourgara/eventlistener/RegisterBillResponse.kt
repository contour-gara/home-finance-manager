package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import dev.kord.common.Color
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.EmbedData
import dev.kord.rest.builder.message.EmbedBuilder
import org.contourgara.application.RegisterBillDto
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError
import org.contourgara.eventlistener.RegisterBillValidation.validateAmount
import org.contourgara.eventlistener.RegisterBillValidation.validateClaimant
import org.contourgara.eventlistener.RegisterBillValidation.validateEmbedData
import org.contourgara.eventlistener.RegisterBillValidation.validateMemo
import kotlin.collections.first
import kotlin.collections.last

@OptIn(ExperimentalRaiseAccumulateApi::class)
@ConsistentCopyVisibility
data class RegisterBillResponse private constructor (
    val id: String,
    val amount: Int,
    val claimant: User,
    val memo: String
) {
    companion object {
        fun from(dto: RegisterBillDto): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            either {
                of(dto.id, dto.amount, User.of(dto.claimant), dto.memo).bind()
            }

        fun from(embedData: EmbedData): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            either {
                accumulate {
                    validateEmbedData(embedData).bindNelOrAccumulate()
                }
                of(
                    id = embedData.fields.orEmpty().first().value,
                    amount = embedData.fields.orEmpty()[1].value.split(" ").first().toInt(),
                    claimant = User.of(embedData.fields.orEmpty()[2].value),
                    memo = embedData.fields.orEmpty().last().value
                ).bind()
            }

        private fun of(id: String, amount: Int, claimant: User, memo: String): Either<NonEmptyList<RegisterBillValidationError>, RegisterBillResponse> =
            either {
                accumulate {
                    validateAmount(amount).bindNelOrAccumulate()
                    validateClaimant(claimant).bindNelOrAccumulate()
                    validateMemo(memo).bindNelOrAccumulate()
                }
                RegisterBillResponse(id, amount, claimant, memo)
            }
    }

    fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(0, 255, 0)
        field(name = "申請 ID だっピ", inline = true, value = { id })
        field(name = "請求金額だっピ", inline = true, value = { "$amount 円" })
        field(name = "請求者だっピ", inline = true, value = { claimant.name.lowercase() })
        field(name = "メモだっピ", inline = true, value = { memo })
    }
}
