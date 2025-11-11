package org.contourgara.infrastructure

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.runBlocking
import org.contourgara.FinanceCoreConfig
import org.contourgara.domain.DiscordClient
import org.contourgara.domain.RegisterBill
import org.springframework.stereotype.Component

@Component
class DiscordClientImpl(
    private val financeCoreConfig: FinanceCoreConfig,
) : DiscordClient {
    private val kord: Kord by lazy {
        runBlocking { Kord(financeCoreConfig.discordBotToken) }
    }
    private val restClient: RestClient by lazy {
        RestClient(KtorRequestHandler(financeCoreConfig.discordBotToken))
    }

    override fun notifyRegisterBill(registerBill: RegisterBill) {
        runBlocking {
            RegisterBillEntity
                .from(registerBill)
                .also {
                    restClient.channel.createMessage(Snowflake(financeCoreConfig.discordChannelId)) {
                        content = "${kord.getUser(it.lender.id)?.mention} から ${kord.getUser(it.borrower.id)?.mention} への請求が登録されました！"
                        embed {
                            title = "詳細っピ"
                            color = Color(0, 255, 0)
                            field {
                                name = "請求 ID"
                                value = it.billId.toString()
                            }
                            field {
                                name = "イベント ID"
                                value = it.eventId
                            }
                        }
                    }
                }
        }
    }
}
