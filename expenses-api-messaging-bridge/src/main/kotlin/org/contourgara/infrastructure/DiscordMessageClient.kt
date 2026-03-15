package org.contourgara.infrastructure

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.runBlocking
import org.contourgara.ExpensesApiMessagingBridgeConfig
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.MessageClient
import org.contourgara.domain.MessageId

class DiscordMessageClient(
    private val expensesApiMessagingBridgeConfig: ExpensesApiMessagingBridgeConfig,
) : MessageClient {
    private val restClient: RestClient by lazy {
        RestClient(requestHandler = KtorRequestHandler(expensesApiMessagingBridgeConfig.discordBotToken))
    }

    override fun replySuccessCreateExpense(
        messageId: MessageId,
        expenseId: ExpenseId,
        expenseEventId: ExpenseEventId,
    ) {
        runBlocking {
            restClient.channel.createMessage(channelId = Snowflake(expensesApiMessagingBridgeConfig.discordChannelId)) {
                messageReference = messageId.value
                content = "支出を登録したっピ！"
                embed {
                    title = "詳細っピ"
                    color = Color(0, 255, 0)
                    field {
                        name = "支出 ID"
                        value = expenseId.value.toString()
                    }
                    field {
                        name = "イベント ID"
                        value = expenseEventId.value.toString()
                    }
                }
            }
        }
    }

    override fun replySuccessDeleteExpense(
        messageId: MessageId,
        expenseId: ExpenseId,
        expenseEventId: ExpenseEventId,
    ) {
        runBlocking {
            restClient.channel.createMessage(channelId = Snowflake(expensesApiMessagingBridgeConfig.discordChannelId)) {
                messageReference = messageId.value
                content = "支出を削除したっピ！"
                embed {
                    title = "詳細っピ"
                    color = Color(0, 255, 0)
                    field {
                        name = "支出 ID"
                        value = expenseId.value.toString()
                    }
                    field {
                        name = "イベント ID"
                        value = expenseEventId.value.toString()
                    }
                }
            }
        }
    }
}
