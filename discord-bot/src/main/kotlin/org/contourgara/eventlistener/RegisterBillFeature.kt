package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import io.konform.validation.Valid
import io.konform.validation.ValidationError
import org.contourgara.application.RegisterBillUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.forEach

object RegisterBillFeature : KoinComponent {
    const val REGISTER_BILL_COMMAND_NAME = "register-bill"
    const val REGISTER_BILL_COMMAND_DESCRIPTION = "請求を登録するっピ"
    const val REGISTER_BILL_SELECT_MENU_ID = "claimant"
    const val REGISTER_BILL_MODAL_ID = "register-bill-memo"
    private const val REGISTER_BILL_COMMAND_ARGUMENT_NAME = "billing-amount"
    private const val REGISTER_BILL_COMMAND_ARGUMENT_DESCRIPTION = "請求金額"
    private val registerBillUseCase: RegisterBillUseCase by inject()

    fun createRegisterBillCommandArgument(): ChatInputCreateBuilder.() -> Unit = {
        integer(REGISTER_BILL_COMMAND_ARGUMENT_NAME, REGISTER_BILL_COMMAND_ARGUMENT_DESCRIPTION) {
            required = true
            minValue = 1
            maxValue = 2147483647
        }
    }

    suspend fun GuildChatInputCommandInteractionCreateEvent.sendSelectUserMessage() = interaction.deferPublicResponse().respond {
        when (val validationResult = RegisterBillRequest.of(interaction.command.integers[REGISTER_BILL_COMMAND_ARGUMENT_NAME]?.toInt()!!)) {
            is Either.Right -> {
                embed(validationResult.value.toEmbedBuilder())
                actionRow {
                    userSelect(REGISTER_BILL_SELECT_MENU_ID) {
                        placeholder = "請求者を選択してっピ"
                    }
                }
            }
            is Either.Left -> embed(validationResult.value.toEmbedBuilder())
        }
    }

    suspend fun SelectMenuInteractionCreateEvent.openBillMemoModal() = interaction.modal("メモ", REGISTER_BILL_MODAL_ID) {
        actionRow {
            textInput(TextInputStyle.Paragraph, interaction.values.first(), "メモ") {
                placeholder = "メモを入力してっピ"
                allowedLength = 1..999
            }
        }
    }

    suspend fun ModalSubmitInteractionCreateEvent.submitBillMemoModal() = interaction.deferPublicMessageUpdate().edit {
        actionRow {
            userSelect(REGISTER_BILL_SELECT_MENU_ID) {
                disabled = true
            }
        }

        when (val validationResult = interaction.textInputs.keys.first().let {
            RegisterBillRequest.of(interaction.message?.embeds?.first()?.data!!, it.toLong(), interaction.textInputs[it]?.value!!)
        }) {
            is Valid -> {
                val registerBillResponse = RegisterBillResponse.fromDto(registerBillUseCase.execute(validationResult.value.toParam()))
                val user = kord.getUser(Snowflake(registerBillResponse.claimant.id))
                content = "${user?.mention} 請求を受け付けたっピ"
                embed(registerBillResponse.toEmbedBuilder())
            }
            else -> embed(validationResult.errors.toEmbedBuilder())
        }
    }

    private fun NonEmptyList<RegisterBillValidation.RegisterBillValidationError>.toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "Bad Request"
        color = Color(255, 0, 0)
        this@toEmbedBuilder.forEach {
            field(name = it.dataPath, inline = true, value = { it.message })
        }
    }

    private fun List<ValidationError>.toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "Bad Request"
        color = Color(255, 0, 0)
        this@toEmbedBuilder.forEach {
            field(name = it.dataPath, inline = true, value = { it.message })
        }
    }
}
