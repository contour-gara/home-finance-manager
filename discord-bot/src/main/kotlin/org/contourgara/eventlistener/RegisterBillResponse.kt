package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import org.contourgara.application.RegisterBillDto

@ConsistentCopyVisibility
data class RegisterBillResponse private constructor (
    val id: String,
    val amount: Int,
    val claimant: User,
    val memo: String
) {
    companion object {
        fun fromDto(dto: RegisterBillDto): RegisterBillResponse =
            RegisterBillResponse(
                dto.id,
                dto.amount,
                User.of(dto.claimant),
                dto.memo
            )
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
