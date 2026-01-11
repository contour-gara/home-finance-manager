package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import org.contourgara.DiscordBotConfig
import org.contourgara.application.CreateExpenseDto
import org.contourgara.application.CreateExpenseParam
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue
import kotlin.text.toInt

object ExpenseFeature : KoinComponent {
    const val CREATE_COMMAND_NAME = "create-expense"
    const val CREATE_COMMAND_DESCRIPTION = "支出を作成するっピ"
    const val CREATE_COMMAND_ARGUMENT_NAME_AMOUNT = "amount"
    const val CREATE_COMMAND_ARGUMENT_DESCRIPTION_AMOUNT = "金額を入力してっピ"
    const val CREATE_COMMAND_ARGUMENT_NAME_YEAR = "select-year"
    const val CREATE_COMMAND_ARGUMENT_DESCRIPTION_YEAR = "年を入力してっピ"
    const val CREATE_COMMAND_ARGUMENT_NAME_MONTH = "select-month"
    const val CREATE_COMMAND_ARGUMENT_DESCRIPTION_MONTH = "月を入力してっピ"
    const val DELETE_COMMAND_NAME = "delete-expense"
    const val DELETE_COMMAND_DESCRIPTION = "支出を削除するっピ"
    const val DELETE_COMMAND_ARGUMENT_NAME_MESSAGE_ID = "message-id"
    const val DELETE_COMMAND_ARGUMENT_DESCRIPTION_MESSAGE_ID = "メッセージ ID を入力してっピ"
    const val SELECT_PAYER_ID = "select-payer"
    const val SELECT_PAYER_PLACEHOLDER = "支払い者を選択してっピ"
    const val SELECT_CATEGORY_ID = "select-category"
    const val SELECT_CATEGORY_PLACEHOLDER = "支出カテゴリーを選択してっピ"
    const val MEMO_BUTTON_ID = "memo-button"
    const val MEMO_MODAL_ID = "memo-modal"
    const val MEMO_MODAL_INPUT_ID = "memo-modal-input"
    const val SUBMIT_BUTTON_ID = "submit-button"
    const val DELETE_BUTTON_ID = "delete-expense-button"
    const val DELETE_BUTTON_LABEL = "削除"
    const val EMBED_FIELD_KEY_AMOUNT = "支出金額"
    const val EMBED_FIELD_KEY_PAYER = "支払い者"
    const val EMBED_FIELD_KEY_CATEGORY = "支出カテゴリー"
    const val EMBED_FIELD_KEY_YEAR = "年"
    const val EMBED_FIELD_KEY_MONTH = "月"
    const val EMBED_FIELD_KEY_MEMO = "メモ"
    const val EMBED_FIELD_KEY_EXPENSE_ID = "支出 ID"
    const val EMBED_FIELD_KEY_EXPENSE_EVENT_ID = "支出イベント ID"
    const val EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID = "支出作成メッセージ ID"
    const val BUTTON_LABEL_MEMO = "メモを入力"
    const val BUTTON_LABEL_SUBMIT_CREATE = "送信"

    private val discordBotConfig: DiscordBotConfig by inject()
    private val createExpenseUseCase: CreateExpenseUseCase by inject()
    private val deleteExpenseUseCase: DeleteExpenseUseCase by inject()

    suspend fun GuildChatInputCommandInteractionCreateEvent.sendSelectParamMessage() =
        when (interaction.channelId) {
            Snowflake(value = discordBotConfig.channelId) ->
                interaction
                    .command
                    .integers
                    .let {
                        CreateExpenseRequest(
                            amount = it[CREATE_COMMAND_ARGUMENT_NAME_AMOUNT]!!.toInt(),
                            payer = null,
                            category = null,
                            year = it[CREATE_COMMAND_ARGUMENT_NAME_YEAR]!!.toInt(),
                            month = it[CREATE_COMMAND_ARGUMENT_NAME_MONTH]!!.toInt(),
                            memo = null,
                        )
                    }
                    .also {
                        interaction
                            .deferPublicResponse()
                            .respond(builder = it.toInteractionResponseModifyBuilder())
                    }
            else ->
                interaction
                    .deferPublicResponse()
                    .respond {
                        content = "${kord.getChannel(Snowflake(discordBotConfig.channelId))?.mention} で実行してっピ"
                    }
        }

    suspend fun GuildChatInputCommandInteractionCreateEvent.sendConfirmDeleteExpenseMessage() =
        when (interaction.channelId) {
            Snowflake(value = discordBotConfig.channelId) ->
                interaction
                    .command
                    .strings
                    .let {
                        DeleteExpenseRequest(
                            messageId = it[DELETE_COMMAND_ARGUMENT_NAME_MESSAGE_ID]!!,
                        )
                    }
                    .also {
                        interaction
                            .deferPublicResponse()
                            .respond(builder = it.toInteractionResponseModifyBuilder(channelId = discordBotConfig.channelId))
                    }
            else ->
                interaction
                    .deferPublicResponse()
                    .respond {
                        content = "${kord.getChannel(Snowflake(discordBotConfig.channelId))?.mention} で実行してっピ"
                    }
        }

    suspend fun SelectMenuInteractionCreateEvent.submitExpensePayer() =
        interaction
            .message
            .embeds
            .first()
            .data
            .let {
                CreateExpenseRequest
                    .fromEmbedData(embedData = it)
                    .copy(payer = interaction.values.first())
            }
            .let {
                interaction
                    .deferPublicMessageUpdate()
                    .edit(builder = it.toInteractionResponseModifyBuilder())
            }

    suspend fun SelectMenuInteractionCreateEvent.submitExpenseCategory() =
        interaction
            .message
            .embeds
            .first()
            .data
            .let {
                CreateExpenseRequest
                    .fromEmbedData(embedData = it)
                    .copy(category = interaction.values.first())
            }
            .let {
                interaction
                    .deferPublicMessageUpdate()
                    .edit(builder = it.toInteractionResponseModifyBuilder())
            }

    suspend fun ButtonInteractionCreateEvent.openExpenseMemoModal() =
        interaction
            .modal(
                title = "メモを入力するっピ",
                customId = MEMO_MODAL_ID,
            ) {
                actionRow {
                    textInput(
                        style = TextInputStyle.Paragraph,
                        customId = MEMO_MODAL_INPUT_ID,
                        label = "メモ",
                    ) {
                        placeholder = "メモを入力してっピ"
                        allowedLength = 1..999
                        required = true
                    }
                }
            }

    suspend fun ModalSubmitInteractionCreateEvent.submitExpenseMemoModal() =
        interaction
            .message!!
            .embeds
            .first()
            .data
            .let {
                CreateExpenseRequest
                    .fromEmbedData(embedData = it)
                    .copy(memo = interaction.textInputs[MEMO_MODAL_INPUT_ID]!!.value)
            }
            .let {
                interaction
                    .deferPublicMessageUpdate()
                    .edit(builder = it.toInteractionResponseModifyBuilder())
            }

    suspend fun ButtonInteractionCreateEvent.submitCreateExpense() =
        interaction
            .message
            .embeds
            .first()
            .data
            .let { CreateExpenseRequest.fromEmbedData(embedData = it) }
            .toParam()
            .let { createExpenseUseCase.execute(createExpenseParam = it) }
            .let { CreateExpenseResponse.fromDto(createExpenseDto = it) }
            .let {
                interaction
                    .deferPublicMessageUpdate()
                    .edit(builder = it.toInteractionResponseModifyBuilder())
            }

    suspend fun ButtonInteractionCreateEvent.submitDeleteExpense() =
        interaction
            .message
            .embeds
            .first()
            .data
            .let { DeleteExpenseRequest.fromEmbedData(embedData = it) }
            .let { request ->
                request.copy(
                    expenseId = kord
                        .getChannelOf<MessageChannel>(
                            id = Snowflake(value = discordBotConfig.channelId),
                        )!!
                        .getMessage(
                            messageId = Snowflake(value = request.messageId),
                        )
                        .embeds
                        .first()
                        .data
                        .fields
                        .orEmpty()
                        .associate { it.name to it.value }
                        [EMBED_FIELD_KEY_EXPENSE_ID]!!
                )
            }
            .let {
                val (expenseId, expenseEventId) = deleteExpenseUseCase.execute(expenseId = it.expenseId!!)
                DeleteExpenseResponse(
                    messageId = it.messageId,
                    expenseId = expenseId,
                    expenseEventId = expenseEventId,
                )
            }
            .let {
                interaction
                    .deferPublicMessageUpdate()
                    .edit(builder = it.toInteractionResponseModifyBuilder(channelId = discordBotConfig.channelId))
            }
}

data class CreateExpenseRequest(
    val amount: Int,
    val payer: String?,
    val category: String?,
    val year: Int,
    val month: Int,
    val memo: String?,
) {
    companion object {
        fun fromEmbedData(embedData: EmbedData): CreateExpenseRequest =
            embedData
                .fields
                .orEmpty()
                .associate { it.name to it.value }
                .let {
                    CreateExpenseRequest(
                        amount = it[ExpenseFeature.EMBED_FIELD_KEY_AMOUNT]!!.parseAmount().toInt(),
                        payer = it[ExpenseFeature.EMBED_FIELD_KEY_PAYER],
                        category = it[ExpenseFeature.EMBED_FIELD_KEY_CATEGORY],
                        year = it[ExpenseFeature.EMBED_FIELD_KEY_YEAR]!!.toInt(),
                        month = it[ExpenseFeature.EMBED_FIELD_KEY_MONTH]!!.toInt(),
                        memo = it[ExpenseFeature.EMBED_FIELD_KEY_MEMO],
                    )
                }
    }

    fun toInteractionResponseModifyBuilder(): InteractionResponseModifyBuilder.() -> Unit = {
        content = "支出の情報を入力してっピ"
        embed(builder = toEmbedBuilder())
        actionRow(builder = toPayerActionRowBuilder())
        actionRow(builder = toCategoryActionRowBuilder())
        actionRow(builder = toMemoActionRowBuilder())
        actionRow(builder = toSubmitActionRowBuilder())
    }

    private fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(red = 255, green = 255, blue = 50)
        field(name = ExpenseFeature.EMBED_FIELD_KEY_AMOUNT, inline = true, value = { amount.formatAmount() })
        payer?.let { field(name = ExpenseFeature.EMBED_FIELD_KEY_PAYER, inline = true, value = { it }) }
        category?.let { field(name = ExpenseFeature.EMBED_FIELD_KEY_CATEGORY, inline = true, value = { it }) }
        field(name = ExpenseFeature.EMBED_FIELD_KEY_YEAR, inline = true, value = { year.toString() })
        field(name = ExpenseFeature.EMBED_FIELD_KEY_MONTH, inline = true, value = { month.toString() })
        memo?.let { field(name = ExpenseFeature.EMBED_FIELD_KEY_MEMO, inline = true, value = { it }) }
    }

    private fun toPayerActionRowBuilder(): ActionRowBuilder.() -> Unit = {
        stringSelect(customId = ExpenseFeature.SELECT_PAYER_ID) {
            placeholder = ExpenseFeature.SELECT_PAYER_PLACEHOLDER
            option(label = Payer.GARA.label, value = Payer.GARA.name)
            option(label = Payer.YUKI.label, value = Payer.YUKI.name)
            option(label = Payer.DIRECT_DEBIT.label, value = Payer.DIRECT_DEBIT.name)
            disabled = payer != null
        }
    }

    private fun toCategoryActionRowBuilder(): ActionRowBuilder.() -> Unit = {
        stringSelect(customId = ExpenseFeature.SELECT_CATEGORY_ID) {
            placeholder = ExpenseFeature.SELECT_CATEGORY_PLACEHOLDER
            option(label = Category.RENT.label, value = Category.RENT.name)
            option(label = Category.UTILITIES.label, value = Category.UTILITIES.name)
            option(label = Category.FOOD.label, value = Category.FOOD.name)
            option(label = Category.DAILY_NEEDS.label, value = Category.DAILY_NEEDS.name)
            option(label = Category.HEALTHCARE.label, value = Category.HEALTHCARE.name)
            option(label = Category.ENTERTAINMENT.label, value = Category.ENTERTAINMENT.name)
            option(label = Category.TRANSPORTATION.label, value = Category.TRANSPORTATION.name)
            option(label = Category.TRAVEL.label, value = Category.TRAVEL.name)
            option(label = Category.OTHER.label, value = Category.OTHER.name)
            disabled = category != null
        }
    }

    private fun toMemoActionRowBuilder(): ActionRowBuilder.() -> Unit = {
        interactionButton(
            customId = ExpenseFeature.MEMO_BUTTON_ID,
            style = ButtonStyle.Secondary,
        ) {
            label = ExpenseFeature.BUTTON_LABEL_MEMO
            disabled = memo != null
        }
    }

    private fun toSubmitActionRowBuilder(): ActionRowBuilder.() -> Unit = {
        interactionButton(
            customId = ExpenseFeature.SUBMIT_BUTTON_ID,
            style = ButtonStyle.Primary,
        ) {
            label = ExpenseFeature.BUTTON_LABEL_SUBMIT_CREATE
            disabled = payer == null || category == null || memo == null
        }
    }

    fun toParam(): CreateExpenseParam =
        CreateExpenseParam(
            amount = amount,
            payer = payer!!,
            category = category!!,
            year = year,
            month = month,
            memo = memo!!,
        )
}

data class CreateExpenseResponse(
    val amount: Int,
    val payer: String,
    val category: String,
    val year: Int,
    val month: Int,
    val memo: String,
    val expenseId: String,
    val expenseEventId: String,
) {
    companion object {
        fun fromDto(createExpenseDto: CreateExpenseDto): CreateExpenseResponse =
            CreateExpenseResponse(
                amount = createExpenseDto.amount,
                payer = createExpenseDto.payer,
                category = createExpenseDto.category,
                year = createExpenseDto.year,
                month = createExpenseDto.month,
                memo = createExpenseDto.memo,
                expenseId = createExpenseDto.expenseId,
                expenseEventId = createExpenseDto.expenseEventId,
            )
    }

    fun toInteractionResponseModifyBuilder(): InteractionResponseModifyBuilder.() -> Unit = {
        content = "支出が作成されたっピ"
        embed {
            title = "入力情報だっピ"
            color = Color(red = 0, green = 255, blue = 0)
            field(name = ExpenseFeature.EMBED_FIELD_KEY_AMOUNT, inline = true, value = { amount.formatAmount() })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_PAYER, inline = true, value = { payer })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_CATEGORY, inline = true, value = { category })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_YEAR, inline = true, value = { year.toString() })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_MONTH, inline = true, value = { month.toString() })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_MEMO, inline = true, value = { memo })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_EXPENSE_ID, inline = true, value = { expenseId })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_EXPENSE_EVENT_ID, inline = true, value = { expenseEventId })
        }
        actionRow {
            stringSelect(customId = ExpenseFeature.SELECT_PAYER_ID) {
                placeholder = ExpenseFeature.SELECT_PAYER_PLACEHOLDER
                option(label = Payer.GARA.label, value = Payer.GARA.name)
                option(label = Payer.YUKI.label, value = Payer.YUKI.name)
                option(label = Payer.DIRECT_DEBIT.label, value = Payer.DIRECT_DEBIT.name)
                disabled = true
            }
        }
        actionRow{
            stringSelect(customId = ExpenseFeature.SELECT_CATEGORY_ID) {
                placeholder = ExpenseFeature.SELECT_CATEGORY_PLACEHOLDER
                option(label = Category.RENT.label, value = Category.RENT.name)
                option(label = Category.UTILITIES.label, value = Category.UTILITIES.name)
                option(label = Category.FOOD.label, value = Category.FOOD.name)
                option(label = Category.DAILY_NEEDS.label, value = Category.DAILY_NEEDS.name)
                option(label = Category.HEALTHCARE.label, value = Category.HEALTHCARE.name)
                option(label = Category.ENTERTAINMENT.label, value = Category.ENTERTAINMENT.name)
                option(label = Category.TRANSPORTATION.label, value = Category.TRANSPORTATION.name)
                option(label = Category.TRAVEL.label, value = Category.TRAVEL.name)
                option(label = Category.OTHER.label, value = Category.OTHER.name)
                disabled = true
            }
        }
        actionRow{
            interactionButton(
                customId = ExpenseFeature.MEMO_BUTTON_ID,
                style = ButtonStyle.Secondary,
            ) {
                label = ExpenseFeature.BUTTON_LABEL_MEMO
                disabled = true
            }
        }
        actionRow{
            interactionButton(
                customId = ExpenseFeature.SUBMIT_BUTTON_ID,
                style = ButtonStyle.Primary,
            ) {
                label = ExpenseFeature.BUTTON_LABEL_SUBMIT_CREATE
                disabled = true
            }
        }
    }
}

enum class Payer(
    val label: String,
) {
    GARA(label = "gara"),
    YUKI(label = "yuki"),
    DIRECT_DEBIT(label = "引き落とし"),
    ;
}

enum class Category(
    val label: String,
) {
    RENT(label = "家賃"),
    UTILITIES(label = "公共料金"),
    FOOD(label = "食費"),
    DAILY_NEEDS(label = "日用品"),
    HEALTHCARE(label = "医療費"),
    ENTERTAINMENT(label = "娯楽費"),
    TRANSPORTATION(label = "交通費"),
    TRAVEL(label = "旅費"),
    OTHER(label = "その他"),
    ;
}

data class DeleteExpenseRequest(
    val messageId: String,
    val expenseId: String? = null,
) {
    companion object {
        fun fromEmbedData(embedData: EmbedData): DeleteExpenseRequest =
            embedData
                .fields
                .orEmpty()
                .associate { it.name to it.value }
                .let {
                    DeleteExpenseRequest(
                        messageId = it[ExpenseFeature.EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID]!!,
                    )
                }
    }

    fun toInteractionResponseModifyBuilder(channelId: String): InteractionResponseModifyBuilder.() -> Unit = {
        content = "削除する支出は https://discord.com/channels/889318150615744523/$channelId/$messageId で間違いないっピか？"
        embed(builder = toEmbedBuilder())
        actionRow(builder = toSubmitActionRowBuilder())
    }

    private fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(red = 255, green = 255, blue = 50)
        field(name = ExpenseFeature.EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID, inline = true, value = { messageId })
        expenseId?.let { field(name = ExpenseFeature.EMBED_FIELD_KEY_EXPENSE_ID, inline = true, value = { it }) }
    }

    private fun toSubmitActionRowBuilder(): ActionRowBuilder.() -> Unit = {
        interactionButton(
            customId = ExpenseFeature.DELETE_BUTTON_ID,
            style = ButtonStyle.Danger,
        ) {
            label = ExpenseFeature.DELETE_BUTTON_LABEL
            disabled = false
        }
    }
}

data class DeleteExpenseResponse(
    val messageId: String,
    val expenseId: String,
    val expenseEventId: String,
) {
    fun toInteractionResponseModifyBuilder(channelId: String): InteractionResponseModifyBuilder.() -> Unit = {
        content = "https://discord.com/channels/889318150615744523/$channelId/$messageId の支出が削除されたっピ"
        embed {
            title = "入力情報だっピ"
            color = Color(red = 0, green = 255, blue = 0)
            field(name = ExpenseFeature.EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID, inline = true, value = { messageId })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_EXPENSE_ID, inline = true, value = { expenseId })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_EXPENSE_EVENT_ID, inline = true, value = { expenseEventId })
        }
        actionRow {
            interactionButton(
                customId = ExpenseFeature.DELETE_BUTTON_ID,
                style = ButtonStyle.Danger,
            ) {
                label = ExpenseFeature.DELETE_BUTTON_LABEL
                disabled = true
            }
        }
    }
}
