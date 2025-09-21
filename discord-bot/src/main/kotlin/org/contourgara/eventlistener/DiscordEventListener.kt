package org.contourgara.eventlistener

import arrow.core.Either
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import org.contourgara.DiscordBotConfig
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_COMMAND_DESCRIPTION
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_COMMAND_NAME
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_MODAL_ID
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_SELECT_MENU_ID
import org.contourgara.eventlistener.RegisterBillFeature.createRegisterBillCommandArgument
import org.contourgara.eventlistener.RegisterBillFeature.openBillMemoModal
import org.contourgara.eventlistener.RegisterBillFeature.sendSelectUserMessage
import org.contourgara.eventlistener.RegisterBillFeature.submitBillMemoModal
import org.contourgara.eventlistener.TestModalFeature.TEST_MODAL_COMMAND_DESCRIPTION
import org.contourgara.eventlistener.TestModalFeature.TEST_MODAL_COMMAND_NAME
import org.contourgara.eventlistener.TestModalFeature.TEST_MODAL_MODAL_ID
import org.contourgara.eventlistener.TestModalFeature.openTestModal
import org.contourgara.eventlistener.TestModalFeature.submitTestModal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DiscordEventListener() : KoinComponent {
    private val discordBotConfig: DiscordBotConfig by inject()
    private lateinit var kord: Kord

    suspend fun start() {
        kord = Kord(discordBotConfig.homeFinanceManagerBotToken)
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
            TEST_MODAL_COMMAND_NAME,
            TEST_MODAL_COMMAND_DESCRIPTION
        )

        kord.createGuildChatInputCommand(
            Snowflake(889318150615744523),
            REGISTER_BILL_COMMAND_NAME,
            REGISTER_BILL_COMMAND_DESCRIPTION,
//            createRegisterBillCommandArgument()
        )

        kord.createGuildChatInputCommand(
            Snowflake(889318150615744523),
            "delete-bill",
            "請求を削除するっピ"
        ) {
            string("message-id", "登録したメッセージ ID") {
                required = true
            }
        }
    }

    private fun createExecuteCommandEvent() {
        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            when (interaction.invokedCommandName) {
                TEST_MODAL_COMMAND_NAME -> openTestModal()
                REGISTER_BILL_COMMAND_NAME -> sendSelectUserMessage()
                "delete-bill" -> {
                    sendMessage()
                    val messageId = interaction.command.strings["message-id"]!!
                    val message = kord.getChannelOf<MessageChannel>(Snowflake(1402331708459581591))?.getMessage(Snowflake(messageId))!!
                    when (val validationResult = RegisterBillResponse.from(message.embeds.first().data)) {
                        is Either.Right -> interaction.deferPublicResponse().respond {
                            content = "削除内容確認"
                            embed(validationResult.value.toEmbedBuilder())
                        }
                        is Either.Left -> interaction.deferPublicResponse().respond {
                            content = "エラー"
                        }
                    }
                }
            }
        }
    }

    private suspend fun sendMessage() {
        val rest = RestClient(KtorRequestHandler(System.getenv("HOME_FINANCE_MANAGER_BOT_TOKEN")))
        rest.channel.createMessage(Snowflake(1402331708459581591)) {
            content = "send message test"
        }
    }

    private fun createSubmitSelect() {
        kord.on<SelectMenuInteractionCreateEvent> {
            when (interaction.componentId) {
                REGISTER_BILL_SELECT_MENU_ID -> openBillMemoModal()
            }
        }
    }

    private fun createSubmitModalEvent() {
        kord.on<ModalSubmitInteractionCreateEvent> {
            when (interaction.modalId) {
                TEST_MODAL_MODAL_ID -> submitTestModal()
                REGISTER_BILL_MODAL_ID -> submitBillMemoModal()
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
