package org.contourgara.infrastructure

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.embed
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.runBlocking
import org.contourgara.FinanceCoreConfig
import org.contourgara.domain.Debt
import org.contourgara.domain.DeleteBill
import org.contourgara.domain.DiscordClient
import org.contourgara.domain.Loan
import org.contourgara.domain.RegisterBill
import org.springframework.stereotype.Component

@Component
class DiscordClientImpl(
    private val financeCoreConfig: FinanceCoreConfig,
) : DiscordClient {
    private val restClient: RestClient by lazy {
        RestClient(KtorRequestHandler(financeCoreConfig.discordBotToken))
    }

    override fun notifyRegisterBill(registerBill: RegisterBill) {
        runBlocking {
            RegisterBillEntity
                .from(registerBill)
                .also {
                    restClient.channel.createMessage(Snowflake(financeCoreConfig.discordChannelId)) {
                        content = "<@${it.lender.id}> から <@${it.borrower.id}> への請求を登録したっピ！"
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

    override fun notifyDeleteBill(deleteBill: DeleteBill) {
        runBlocking {
            DeleteBillEntity
                .from(deleteBill)
                .also {
                    restClient.channel.createMessage(Snowflake(financeCoreConfig.discordChannelId)) {
                        content = "<@${it.lender.id}> から <@${it.borrower.id}> への請求を削除したっピ！"
                        embed {
                            title = "詳細っピ"
                            color = Color(255, 0, 0)
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

    override fun notifyOffsetBalance(loan: Loan) {
        runBlocking {
            LoanEntity
                .from(loan)
                .also {
                    restClient.channel.createMessage(Snowflake(financeCoreConfig.discordChannelId)) {
                        content = "<@${it.lender.id}> は <@${it.borrower.id}> に${it.displayAmount}貸してるっピ"
                        embed {
                            title = "詳細っピ"
                            color = Color(0, 255, 0)
                            field {
                                name = "最新イベント ID"
                                value = it.lastEventId
                            }
                        }
                    }
                }
        }
    }

    override fun notifyOffsetBalance(debt: Debt) {
        runBlocking {
            DebtEntity
                .from(debt)
                .also {
                    restClient.channel.createMessage(Snowflake(financeCoreConfig.discordChannelId)) {
                        content = "<@${it.lender.id}> は <@${it.borrower.id}> に${it.displayAmount}借りてるっピ"
                        embed {
                            title = "詳細っピ"
                            color = Color(0, 255, 0)
                            field {
                                name = "最新イベント ID"
                                value = it.lastEventId
                            }
                        }
                    }
                }
        }
    }
}
