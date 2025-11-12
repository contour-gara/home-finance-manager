package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import org.contourgara.DiscordBotConfig
import org.contourgara.application.DeleteBillUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

object DeleteBillFeature : KoinComponent {
    const val DELETE_BILL_COMMAND_NAME = "delete-bill"
    const val DELETE_BILL_COMMAND_DESCRIPTION = "請求を削除するっピ"
    const val DELETE_BILL_COMMAND_ARGUMENT = "message-id"
    const val DELETE_BILL_COMMAND_ARGUMENT_DESCRIPTION = "登録したメッセージ ID"
    const val DELETE_BILL_BUTTON_ID = "delete-bill-button"
    private val deleteBillUseCase: DeleteBillUseCase by inject()
    private val discordBotConfig: DiscordBotConfig by inject()

    suspend fun GuildChatInputCommandInteractionCreateEvent.sendConfirmDeleteMessage() =
        interaction.deferPublicResponse().respond {
            when (interaction.channelId) {
                Snowflake(discordBotConfig.channelId) -> {
                    interaction.command.strings[DELETE_BILL_COMMAND_ARGUMENT]!!
                        .let {
                            kord.getChannelOf<MessageChannel>(Snowflake(1402331708459581591))?.getMessage(Snowflake(it))!!
                        }
                        .let { DeleteBillRequest.from(it.embeds.first().data) }
                        .let {
                            when (it) {
                                is Either.Left -> {
                                    content = "請求情報の取得に失敗したっピ"
                                    embed(it.value.toEmbedBuilder())
                                }
                                is Either.Right -> {
                                    content = "削除内容確認して欲しいっピ"
                                    embed(it.value.toEmbedBuilder())
                                    actionRow {
                                        interactionButton(
                                            customId = DELETE_BILL_BUTTON_ID,
                                            style = ButtonStyle.Danger,
                                        ) {
                                            label = "削除"
                                        }
                                    }
                                }
                            }
                        }
                }
                else -> content = "${kord.getChannel(Snowflake(discordBotConfig.channelId))?.mention} で実行してっピ"
            }
        }

    suspend fun ButtonInteractionCreateEvent.pushDeleteBillButton() =
        interaction.message.embeds.first().data
            .let { DeleteBillResponse.from(it) }
            .let {
                when (it) {
                    is Either.Left ->
                        interaction.deferPublicMessageUpdate().edit {
                            content = "請求情報の取得に失敗したっピ"
                            embed(it.value.toEmbedBuilder())
                            actionRow {
                                interactionButton(
                                    customId = DELETE_BILL_BUTTON_ID,
                                    style = ButtonStyle.Secondary,
                                ) {
                                    label = "削除"
                                    disabled = true
                                }
                            }
                        }
                    is Either.Right -> {
                        deleteBillUseCase.execute(it.value.toParam())
                        interaction.deferPublicMessageUpdate().edit {
                            content = "${kord.getUser(it.value.borrowerId)?.mention} 請求が削除されたっピ"
                            embed(it.value.toEmbedBuilder())
                            actionRow {
                                interactionButton(
                                    customId = DELETE_BILL_BUTTON_ID,
                                    style = ButtonStyle.Success,
                                ) {
                                    label = "削除"
                                    disabled = true
                                }
                            }
                        }
                    }
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
