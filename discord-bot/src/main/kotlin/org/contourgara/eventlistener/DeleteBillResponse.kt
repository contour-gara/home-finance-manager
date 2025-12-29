package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.EmbedBuilder
import org.contourgara.application.DeleteBillDto
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError

@OptIn(ExperimentalRaiseAccumulateApi::class)
@ConsistentCopyVisibility
data class DeleteBillResponse private constructor (
    private val billId: String,
    val registerBillMessageId: Snowflake,
) {
    companion object {
        fun from(dto: DeleteBillDto): Either<NonEmptyList<RegisterBillValidationError>, DeleteBillResponse> =
            of(
                billId = dto.billId.toString(),
                registerBillMessageId = dto.registerBillMessageId,
            )

        private fun of(billId: String, registerBillMessageId: Snowflake): Either<NonEmptyList<RegisterBillValidationError>, DeleteBillResponse> =
            either {
                accumulate {
                }
                DeleteBillResponse(
                    billId = billId,
                    registerBillMessageId = registerBillMessageId,
                )
            }
    }

    fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(0, 255, 0)
        field(name = "請求 ID", inline = true, value = { billId })
        field(name = "請求登録メッセージ ID", inline = true, value = { registerBillMessageId.toString() })
    }
}
