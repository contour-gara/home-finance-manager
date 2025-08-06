package org.contourgara.eventlistener

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent

suspend fun listenDiscordEvent() {
    val kord = Kord(System.getenv("HOME_FINANCE_MANAGER_BOT_TOKEN"))

    kord.on<MessageCreateEvent> {
        println("メッセージ認識")
        if (message.author?.isBot != false) return@on
        if (message.content != "タコピー") return@on
        println("メッセージ条件パス")
        message.channel.createMessage("ハッピーだっピか！？")
    }

    kord.createGuildChatInputCommand(
        Snowflake(889318150615744523),
        "hello",
        "hello"
    )

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        println("コマンド実行を認識")
        val response = interaction.deferPublicResponse()
        response.respond { content = "たこぴーだっぴ" }
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
