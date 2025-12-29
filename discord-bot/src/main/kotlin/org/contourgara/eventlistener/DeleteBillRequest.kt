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
import org.contourgara.application.DeleteBillParam
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError
import ulid.ULID
import kotlin.collections.first

@OptIn(ExperimentalRaiseAccumulateApi::class)
@ConsistentCopyVisibility
data class DeleteBillRequest private constructor (
    private val billId: ULID,
    val registerBillMessageId: Snowflake,
) {
    companion object {
        fun from(embedData: EmbedData, registerBillMessageId: Snowflake): Either<NonEmptyList<RegisterBillValidationError>, DeleteBillRequest> =
            either {
                accumulate {
                    // TODO: 請求 ID があることのバリデーション
//                    validateEmbedData(embedData).bindNelOrAccumulate()
                }
                of(
                    billId = embedData.fields.orEmpty().first().value,
                    registerBillMessageId = registerBillMessageId,
                ).bind()
            }

        fun fromEnbedData(embedData: EmbedData): Either<NonEmptyList<RegisterBillValidationError>, DeleteBillRequest> =
            either {
                accumulate {
                    // FIXME: 削除用のバリデーションが必要
//                    validateEmbedData(embedData).bindNelOrAccumulate()
                }
                of(
                    billId = embedData.fields.orEmpty().first().value,
                    registerBillMessageId = Snowflake(embedData.fields.orEmpty().last().value),
                ).bind()
            }

        private fun of(billId: String, registerBillMessageId: Snowflake): Either<NonEmptyList<RegisterBillValidationError>, DeleteBillRequest> =
            either {
                accumulate {
//                    validateAmount(amount).bindNelOrAccumulate()
//                    validateLender(lender).bindNelOrAccumulate()
//                    validateBorrower(borrower).bindNelOrAccumulate()
//                    validateLenderAndBorrower(lender, borrower).bindNelOrAccumulate()
//                    validateMemo(memo).bindNelOrAccumulate()
                    // TODO: 請求 ID が ULID 形式であることのバリデーション
                }
                DeleteBillRequest(
                    billId = ULID.parseULID(billId),
                    registerBillMessageId = registerBillMessageId,
                )
            }
    }

    fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(255, 255, 50)
        field {
            name = "請求 ID"
            inline = true
            value = billId.toString()
        }
        field {
            name = "請求登録メッセージ ID"
            inline = true
            value = registerBillMessageId.toString()
        }
    }

    fun toParam(): DeleteBillParam =
        DeleteBillParam(
            billId = billId,
            registerBillMessageId = registerBillMessageId,
        )
}
