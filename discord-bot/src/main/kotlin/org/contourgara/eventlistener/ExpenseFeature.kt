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
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.actionRow
import dev.kord.rest.builder.component.option
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.builder.message.modify.InteractionResponseModifyBuilder
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.contourgara.DiscordBotConfig
import org.contourgara.application.CreateExpenseDto
import org.contourgara.application.CreateExpenseParam
import org.contourgara.application.CreateExpenseUseCase
import org.contourgara.application.DeleteExpenseUseCase
import org.contourgara.eventlistener.ExpenseFeature.CREATE_COMMAND_ARGUMENT_NAME_AMOUNT
import org.contourgara.eventlistener.ExpenseFeature.CREATE_COMMAND_ARGUMENT_NAME_DAY
import org.contourgara.eventlistener.ExpenseFeature.CREATE_COMMAND_ARGUMENT_NAME_MONTH
import org.contourgara.eventlistener.ExpenseFeature.CREATE_COMMAND_ARGUMENT_NAME_YEAR
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ExpenseFeature : KoinComponent {
    const val CREATE_COMMAND_NAME = "create-expense"
    const val CREATE_COMMAND_DESCRIPTION = "支出を作成するっピ"
    const val CREATE_COMMAND_ARGUMENT_NAME_AMOUNT = "amount"
    const val CREATE_COMMAND_ARGUMENT_DESCRIPTION_AMOUNT = "金額を入力してっピ"
    const val CREATE_COMMAND_ARGUMENT_NAME_YEAR = "year"
    const val CREATE_COMMAND_ARGUMENT_DESCRIPTION_YEAR = "年を入力してっピ"
    const val CREATE_COMMAND_ARGUMENT_NAME_MONTH = "month"
    const val CREATE_COMMAND_ARGUMENT_DESCRIPTION_MONTH = "月を入力してっピ"
    const val CREATE_COMMAND_ARGUMENT_NAME_DAY = "day"
    const val CREATE_COMMAND_ARGUMENT_DESCRIPTION_DAY = "月を入力してっピ"
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
    const val MEMO_MODAL_MEMO_INPUT_ID = "memo-modal-memo-input"
    const val SUBMIT_BUTTON_ID = "submit-button"
    const val DELETE_BUTTON_ID = "delete-expense-button"
    const val DELETE_BUTTON_LABEL = "削除"
    const val EMBED_FIELD_KEY_AMOUNT = "支出金額"
    const val EMBED_FIELD_KEY_PAYER = "支払い者"
    const val EMBED_FIELD_KEY_CATEGORY = "支出カテゴリー"
    const val EMBED_FIELD_KEY_LOCAL_DATE = "日付"
    const val EMBED_FIELD_KEY_MEMO = "メモ"
    const val EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID = "支出作成メッセージ ID"
    const val BUTTON_LABEL_MEMO = "メモを入力"
    const val BUTTON_LABEL_SUBMIT_CREATE = "送信"

    private val discordBotConfig: DiscordBotConfig by inject()
    private val createExpenseUseCase: CreateExpenseUseCase by inject()
    private val deleteExpenseUseCase: DeleteExpenseUseCase by inject()

    suspend fun GuildChatInputCommandInteractionCreateEvent.sendSelectParamMessage() =
        when (interaction.channelId) {
            Snowflake(value = discordBotConfig.channelId) ->
                runCatching {
                    CreateExpenseRequest.from(interactionCommand = interaction.command)
                }
                    .onSuccess {
                        interaction
                            .deferPublicResponse()
                            .respond(builder = it.toInteractionResponseModifyBuilder())
                    }
                    .onFailure {
                        interaction
                            .deferPublicResponse()
                            .respond {
                                content = it.message
                            }
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
                            createMessageId = Snowflake(value = it[DELETE_COMMAND_ARGUMENT_NAME_MESSAGE_ID]!!),
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
                label(label = "メモ") {
                    textInput(
                        style = TextInputStyle.Paragraph,
                        customId = MEMO_MODAL_MEMO_INPUT_ID,
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
                    .copyMemo(
                        memo = interaction.textInputs[MEMO_MODAL_MEMO_INPUT_ID]!!.value!!,
                    )
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
            .toParam(messageId = interaction.message.id)
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
            .let { deleteExpenseUseCase.execute(createMessageId = it.createMessageId, deleteMessageId = interaction.message.id) }
            .let { DeleteExpenseResponse(createMessageId = it) }
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
    val localDate: LocalDate,
    val memo: String?,
) {
    companion object {
        fun from(interactionCommand: InteractionCommand): CreateExpenseRequest =
            CreateExpenseRequest(
                amount = interactionCommand.integers[CREATE_COMMAND_ARGUMENT_NAME_AMOUNT]!!.toInt(),
                payer = null,
                category = null,
                localDate = LocalDate(
                    year = interactionCommand.integers[CREATE_COMMAND_ARGUMENT_NAME_YEAR]!!.toInt(),
                    month = interactionCommand.integers[CREATE_COMMAND_ARGUMENT_NAME_MONTH]!!.toInt(),
                    day = interactionCommand.integers[CREATE_COMMAND_ARGUMENT_NAME_DAY]!!.toInt(),
                ),
                memo = null,
            )

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
                        localDate = LocalDate.parse(input = it[ExpenseFeature.EMBED_FIELD_KEY_LOCAL_DATE]!!),
                        memo = it[ExpenseFeature.EMBED_FIELD_KEY_MEMO],
                    )
                }
    }

    fun copyMemo(memo: String): CreateExpenseRequest =
        copy(
            memo = """
                ${localDate.month.number}/${localDate.day}
                $memo
            """.trimIndent(),
        )

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
        field(name = ExpenseFeature.EMBED_FIELD_KEY_LOCAL_DATE, inline = true, value = { localDate.toString() })
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

    fun toParam(messageId: Snowflake): CreateExpenseParam =
        CreateExpenseParam(
            messageId = messageId,
            amount = amount,
            payer = payer!!,
            category = category!!,
            year = localDate.year,
            month = localDate.month.number,
            day = localDate.day,
            memo = memo!!,
        )
}

data class CreateExpenseResponse(
    val amount: Int,
    val payer: String,
    val category: String,
    val localDate: LocalDate,
    val memo: String,
) {
    companion object {
        fun fromDto(createExpenseDto: CreateExpenseDto): CreateExpenseResponse =
            CreateExpenseResponse(
                amount = createExpenseDto.amount,
                payer = createExpenseDto.payer,
                category = createExpenseDto.category,
                localDate = LocalDate(
                    year = createExpenseDto.year,
                    month = createExpenseDto.month,
                    day = createExpenseDto.day,
                ),
                memo = createExpenseDto.memo,
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
            field(name = ExpenseFeature.EMBED_FIELD_KEY_LOCAL_DATE, inline = true, value = { localDate.toString() })
            field(name = ExpenseFeature.EMBED_FIELD_KEY_MEMO, inline = true, value = { memo })
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
    val createMessageId: Snowflake,
) {
    companion object {
        fun fromEmbedData(embedData: EmbedData): DeleteExpenseRequest =
            embedData
                .fields
                .orEmpty()
                .associate { it.name to it.value }
                .let {
                    DeleteExpenseRequest(
                        createMessageId = Snowflake(value = it[ExpenseFeature.EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID]!!),
                    )
                }
    }

    fun toInteractionResponseModifyBuilder(channelId: String): InteractionResponseModifyBuilder.() -> Unit = {
        content = "削除する支出は https://discord.com/channels/889318150615744523/$channelId/$createMessageId で間違いないっピか？"
        embed(builder = toEmbedBuilder())
        actionRow(builder = toSubmitActionRowBuilder())
    }

    private fun toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "入力情報だっピ"
        color = Color(red = 255, green = 255, blue = 50)
        field(name = ExpenseFeature.EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID, inline = true, value = { createMessageId.value.toString() })
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
    val createMessageId: Snowflake,
) {
    fun toInteractionResponseModifyBuilder(channelId: String): InteractionResponseModifyBuilder.() -> Unit = {
        content = "https://discord.com/channels/889318150615744523/$channelId/$createMessageId の支出が削除されたっピ"
        embed {
            title = "入力情報だっピ"
            color = Color(red = 0, green = 255, blue = 0)
            field(name = ExpenseFeature.EMBED_FIELD_KEY_CREATE_EXPENSE_MESSAGE_ID, inline = true, value = { createMessageId.value.toString() })
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
