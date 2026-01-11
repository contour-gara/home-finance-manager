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
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.string
import org.contourgara.DiscordBotConfig
import org.contourgara.eventlistener.ExpenseFeature.openExpenseMemoModal
import org.contourgara.eventlistener.ExpenseFeature.sendSelectParamMessage
import org.contourgara.eventlistener.ExpenseFeature.submitCreateExpense
import org.contourgara.eventlistener.ExpenseFeature.submitExpenseCategory
import org.contourgara.eventlistener.ExpenseFeature.submitExpenseMemoModal
import org.contourgara.eventlistener.ExpenseFeature.submitExpensePayer
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_BUTTON_ID
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_ARGUMENT
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_ARGUMENT_DESCRIPTION
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_DESCRIPTION
import org.contourgara.eventlistener.DeleteBillFeature.DELETE_BILL_COMMAND_NAME
import org.contourgara.eventlistener.DeleteBillFeature.pushDeleteBillButton
import org.contourgara.eventlistener.DeleteBillFeature.sendConfirmDeleteMessage
import org.contourgara.eventlistener.ExpenseFeature.sendConfirmDeleteExpenseMessage
import org.contourgara.eventlistener.ExpenseFeature.submitDeleteExpense
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_COMMAND_DESCRIPTION
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_COMMAND_NAME
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_MODAL_ID
import org.contourgara.eventlistener.RegisterBillFeature.REGISTER_BILL_SELECT_MENU_ID
import org.contourgara.eventlistener.RegisterBillFeature.openBillMemoModal
import org.contourgara.eventlistener.RegisterBillFeature.sendSelectUserMessage
import org.contourgara.eventlistener.RegisterBillFeature.submitBillMemoModal
import org.contourgara.eventlistener.ShowBalanceFeature.SHOW_BALANCE_COMMAND_DESCRIPTION
import org.contourgara.eventlistener.ShowBalanceFeature.SHOW_BALANCE_COMMAND_NAME
import org.contourgara.eventlistener.ShowBalanceFeature.requestShowBalance
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

        kord.createGuildChatInputCommand(
            guildId = Snowflake(889318150615744523),
            name = SHOW_BALANCE_COMMAND_NAME,
            description = SHOW_BALANCE_COMMAND_DESCRIPTION,
        )

        kord.createGuildChatInputCommand(
            guildId = Snowflake(889318150615744523),
            name = ExpenseFeature.CREATE_COMMAND_NAME,
            description = ExpenseFeature.CREATE_COMMAND_DESCRIPTION,
        ) {
            integer(
                name = ExpenseFeature.CREATE_COMMAND_ARGUMENT_NAME_AMOUNT,
                description = ExpenseFeature.CREATE_COMMAND_ARGUMENT_DESCRIPTION_AMOUNT,
            ) {
                required = true
                minValue = 0
                maxValue = Int.MAX_VALUE.toLong()
            }
            integer(
                name = ExpenseFeature.CREATE_COMMAND_ARGUMENT_NAME_YEAR,
                description = ExpenseFeature.CREATE_COMMAND_ARGUMENT_DESCRIPTION_YEAR,
            ) {
                required = true
                minValue = 2026
                maxValue = 2026
            }
            integer(
                name = ExpenseFeature.CREATE_COMMAND_ARGUMENT_NAME_MONTH,
                description = ExpenseFeature.CREATE_COMMAND_ARGUMENT_DESCRIPTION_MONTH,
            ) {
                required = true
                minValue = 1
                maxValue = 12
            }
        }

        kord.createGuildChatInputCommand(
            guildId = Snowflake(value = 889318150615744523),
            name = ExpenseFeature.DELETE_COMMAND_NAME,
            description = ExpenseFeature.DELETE_COMMAND_DESCRIPTION,
        ) {
            string(
                name = ExpenseFeature.DELETE_COMMAND_ARGUMENT_NAME_MESSAGE_ID,
                description = ExpenseFeature.DELETE_COMMAND_ARGUMENT_DESCRIPTION_MESSAGE_ID,
            ) {
                required = true
            }
        }
    }

    private fun createExecuteCommandEvent() {
        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            when (interaction.invokedCommandName) {
                TEST_MODAL_COMMAND_NAME -> openTestModal()
                REGISTER_BILL_COMMAND_NAME -> sendSelectUserMessage()
                DELETE_BILL_COMMAND_NAME -> sendConfirmDeleteMessage()
                SHOW_BALANCE_COMMAND_NAME -> requestShowBalance()
                ExpenseFeature.CREATE_COMMAND_NAME -> sendSelectParamMessage()
                ExpenseFeature.DELETE_COMMAND_NAME -> sendConfirmDeleteExpenseMessage()
            }
        }
    }

    private fun createSubmitSelect() {
        kord.on<SelectMenuInteractionCreateEvent> {
            when (interaction.componentId) {
                REGISTER_BILL_SELECT_MENU_ID -> openBillMemoModal()
                ExpenseFeature.SELECT_PAYER_ID -> submitExpensePayer()
                ExpenseFeature.SELECT_CATEGORY_ID -> submitExpenseCategory()
            }
        }
    }

    private fun createSubmitModalEvent() {
        kord.on<ModalSubmitInteractionCreateEvent> {
            when (interaction.modalId) {
                TEST_MODAL_MODAL_ID -> submitTestModal()
                REGISTER_BILL_MODAL_ID -> submitBillMemoModal()
                ExpenseFeature.MEMO_MODAL_ID -> submitExpenseMemoModal()
            }
        }
    }

    private fun createButtonInteractionEvent() {
        kord.on<ButtonInteractionCreateEvent> {
            when (interaction.componentId) {
                DELETE_BILL_BUTTON_ID -> pushDeleteBillButton()
                ExpenseFeature.MEMO_BUTTON_ID -> openExpenseMemoModal()
                ExpenseFeature.SUBMIT_BUTTON_ID -> submitCreateExpense()
                ExpenseFeature.DELETE_BUTTON_ID -> submitDeleteExpense()
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
