package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import org.contourgara.DiscordBotConfig
import org.contourgara.application.RegisterBillUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.collections.forEach

object RegisterBillFeature : KoinComponent {
    const val REGISTER_BILL_COMMAND_NAME = "register-bill"
    const val REGISTER_BILL_COMMAND_DESCRIPTION = "請求を登録するっピ"
    const val REGISTER_BILL_SELECT_MENU_ID = "claimant"
    const val REGISTER_BILL_MODAL_ID = "register-bill-memo"
    private const val REGISTER_BILL_MODAL_AMOUNT_ID = "amount"
    private val registerBillUseCase: RegisterBillUseCase by inject()
    private val discordBotConfig: DiscordBotConfig by inject()

    suspend fun GuildChatInputCommandInteractionCreateEvent.sendSelectUserMessage() = interaction.deferPublicResponse().respond {
        when (interaction.channelId) {
            Snowflake(discordBotConfig.channelId) -> {
                actionRow {
                    userSelect(REGISTER_BILL_SELECT_MENU_ID) {
                        placeholder = "請求先を選択してっピ"
                    }
                }
            }

            else -> content = "${kord.getChannel(Snowflake(discordBotConfig.channelId))?.mention} で実行してっピ"
        }
    }

    suspend fun SelectMenuInteractionCreateEvent.openBillMemoModal() = interaction.modal("請求情報を入力するっピ", REGISTER_BILL_MODAL_ID) {
        actionRow {
            textInput(TextInputStyle.Short, REGISTER_BILL_MODAL_AMOUNT_ID, "請求金額") {
                placeholder = "請求金額を入力するっピ (半角数字)"
                allowedLength = 1..10
            }
        }
        actionRow {
            textInput(TextInputStyle.Paragraph, interaction.values.first(), "メモ") {
                placeholder = "メモを入力するっピ"
                allowedLength = 1..999
            }
        }
    }

    suspend fun ModalSubmitInteractionCreateEvent.submitBillMemoModal() = interaction.deferPublicMessageUpdate().edit {
        actionRow {
            userSelect(REGISTER_BILL_SELECT_MENU_ID) {
                placeholder = "請求先を選択してっピ"
                disabled = true
            }
        }

        when (val validationResult = interaction.textInputs.keys.last().let {
            RegisterBillRequest.of(
                amount = interaction.textInputs[REGISTER_BILL_MODAL_AMOUNT_ID]?.value!!,
                lender = interaction.user.id.value.toLong(),
                borrower = it.toLong(),
                memo = interaction.textInputs[it]?.value!!,
            )
        }.flatMap {
            RegisterBillResponse.from(registerBillUseCase.execute(it.toParam()))
        }) {
            is Either.Right -> {
                content = "${kord.getUser(validationResult.value.getBorrowerId())?.mention} 請求が届いたっピ"
                embed(validationResult.value.toEmbedBuilder())
            }
            is Either.Left -> embed(validationResult.value.toEmbedBuilder())
        }
    }

    private fun NonEmptyList<RegisterBillValidation.RegisterBillValidationError>.toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "Bad Request だっピ"
        color = Color(255, 0, 0)
        this@toEmbedBuilder.forEach {
            field(name = it.dataPath, inline = true, value = { it.message })
        }
    }
}
