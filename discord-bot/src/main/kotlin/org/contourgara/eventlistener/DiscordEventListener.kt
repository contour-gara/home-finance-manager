package org.contourgara.eventlistener

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import org.contourgara.DiscordBotConfig
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_BUTTON_ID
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_ARGUMENT
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_ARGUMENT_DESCRIPTION
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_DESCRIPTION
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_NAME
import org.contourgara.eventlistener.DeleteBillFeature.pushDeleteBillButton
import org.contourgara.eventlistener.DeleteBillFeature.sendConfirmDeleteMessage
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_COMMAND_DESCRIPTION
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_COMMAND_NAME
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_MODAL_ID
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_SELECT_MENU_ID
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

object DiscordEventListener : KoinComponent {
    private val discordBotConfig: DiscordBotConfig by inject()
    private lateinit var kord: Kord

    suspend fun start() {
        kord = Kord(discordBotConfig.botToken)
        createMessageEvent()
        createCommand()
        createExecuteCommandEvent()
        createSubmitModalEvent()
        createSubmitSelect()
        createButtonInteractionEvent()
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
        )

        kord.createGuildChatInputCommand(
            Snowflake(889318150615744523),
            DELETE_BILL_COMMAND_NAME,
            DELETE_BILL_COMMAND_DESCRIPTION,
        ) {
            string(DELETE_BILL_COMMAND_ARGUMENT, DELETE_BILL_COMMAND_ARGUMENT_DESCRIPTION) {
                required = true
            }
        }

        // TODO: add offset-balance command
    }

    private fun createExecuteCommandEvent() {
        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            when (interaction.invokedCommandName) {
                TEST_MODAL_COMMAND_NAME -> openTestModal()
                REGISTER_BILL_COMMAND_NAME -> sendSelectUserMessage()
                DELETE_BILL_COMMAND_NAME -> sendConfirmDeleteMessage()
            }
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

    private fun createButtonInteractionEvent() {
        kord.on<ButtonInteractionCreateEvent> {
            when (interaction.componentId) {
                DELETE_BILL_BUTTON_ID -> pushDeleteBillButton()
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
