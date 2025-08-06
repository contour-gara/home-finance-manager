package org.contourgara.eventlistener

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent

suspend fun listenDiscordEvent() {
    val kord = Kord(System.getenv("HOME_FINANCE_MANAGER_BOT_TOKEN"))

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot!!) return@on
        if (message.content != "タコピー") return@on
        message.channel.createMessage("ハッピーだっピか！？")
    }

    kord.createGuildChatInputCommand(
        Snowflake(889318150615744523),
        "modal",
        "modal test"
    )

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        if (interaction.invokedCommandName == "modal") {
            if (interaction.channel.id != Snowflake(1402331708459581591)) interaction.deferPublicResponse().respond { content = "test で実行してね" }
            interaction.modal("テストモーダル", "test-modal") {
                actionRow {
                    textInput(TextInputStyle.Short, "memo", "メモ") {
                        placeholder = "メモを入力"
                        allowedLength = 1..100
                    }
                }
            }
        }
    }

    kord.on<ModalSubmitInteractionCreateEvent> {
        when (interaction.modalId) {
            "test-modal" -> {
                val memo = interaction.textInputs["memo"]?.value
                println(memo)
                interaction.deferPublicResponse().respond { content = "受け付けたっぴ: $memo" }
            }
        }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
