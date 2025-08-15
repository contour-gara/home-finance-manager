package org.contourgara.eventlistener

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed

class DiscordEventListener() {
    private lateinit var kord: Kord

    suspend fun start() {
        kord = Kord(System.getenv("HOME_FINANCE_MANAGER_BOT_TOKEN"))
        createMessageEvent()
        createCommand()
        createExecuteCommandEvent()
        createSubmitModalEvent()
        createSubmitSelect()
        login()
    }

    private fun createMessageEvent() {
        kord.on<MessageCreateEvent> {
            if (message.author?.isBot!!) return@on
            if (message.content != "タコピー") return@on
            message.channel.createMessage("ハッピーだっピか！？")
        }
    }

    private suspend fun createCommand() {
        kord.createGuildChatInputCommand(
            Snowflake(889318150615744523),
            "modal",
            "modal test"
        )

        kord.createGuildChatInputCommand(
            Snowflake(889318150615744523),
            "register-bill",
            "請求を登録するっピ"
        ) {
            integer("billing-amount", "請求金額") {
                required = true
                minValue = 1
            }
        }
    }

    private fun createExecuteCommandEvent() {
        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            when (interaction.invokedCommandName) {
                "modal" -> openTestModal()
                "register-bill" -> {
                    interaction.deferPublicResponse().respond {
                        embed {
                            title = "入力情報だっピ"
                            field(name = "請求金額だっピ", inline = true, value = { "${interaction.command.integers["billing-amount"].toString()} 円" })
                        }

                        actionRow {
                            userSelect("claimant") {
                                placeholder = "請求者を選択してっピ"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createSubmitSelect() {
        kord.on<SelectMenuInteractionCreateEvent> {
            when (interaction.componentId) {
                "claimant" -> {
                    interaction.modal("メモ", "bill-memo") {
                        actionRow {
                            textInput(TextInputStyle.Paragraph, interaction.values.first(), "メモ") {
                                placeholder = "メモを入力してっピ"
                                allowedLength = 1..999
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createSubmitModalEvent() {
        kord.on<ModalSubmitInteractionCreateEvent> {
            when (interaction.modalId) {
                "modal" -> submitTestModal()
                "bill-memo" -> interaction.deferPublicMessageUpdate().edit {
                    val userId = interaction.textInputs.keys.first()
                    val user = kord.getUser(Snowflake(userId))
                    content = "${user?.mention} 請求を受け付けたっピ"
                    embed {
                        title = interaction.message?.embeds?.first()?.title
                        field(name = interaction.message?.embeds?.first()?.fields?.first()?.name!!, inline = true, value = { interaction.message?.embeds?.first()?.fields?.first()?.value!! })
                        field(name = "請求者だっピ", inline = true, value = { user?.username!! })
                        field(name = "メモだっピ", inline = true, value = { interaction.textInputs[userId]?.value!! })
                    }
                    actionRow {
                        userSelect("claimant") {
                            disabled = true
                        }
                    }
                }
            }
        }
    }

    private suspend fun login() {
        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }
}
