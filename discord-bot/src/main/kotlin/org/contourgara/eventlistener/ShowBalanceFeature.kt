package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import org.contourgara.DiscordBotConfig
import org.contourgara.application.ShowBalanceParam
import org.contourgara.application.ShowBalanceUseCase
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError
import org.contourgara.eventlistener.RegisterBillValidation.validateLender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ShowBalanceFeature : KoinComponent {
    const val SHOW_BALANCE_COMMAND_NAME = "show-balance"
    const val SHOW_BALANCE_COMMAND_DESCRIPTION = "残高を問い合わせるっピ"
    private val showBalanceUseCase: ShowBalanceUseCase by inject()
    private val discordBotConfig: DiscordBotConfig by inject()

    suspend fun GuildChatInputCommandInteractionCreateEvent.requestShowBalance() =
        when (interaction.channelId) {
            Snowflake(discordBotConfig.channelId) -> {
                interaction.user.id.value.toLong()
                    .let { ShowBalanceRequest.from(it) }
                    .map { it.toParam() }
                    .map { showBalanceUseCase.execute(it) }
                    .also {
                        interaction.deferPublicResponse().respond {
                            when (it) {
                                is Either.Left -> {
                                    content = "リクエストが失敗したっピ"
                                    embed(it.value.toEmbedBuilder())
                                }
                                is Either.Right -> {
                                    content = "リクエストが成功したっピ"
                                }
                            }
                        }
                    }
                    .let { Unit }
            }
            else -> interaction.deferPublicResponse().respond {
                content = "${kord.getChannel(Snowflake(discordBotConfig.channelId))?.mention} で実行してっピ"
            }
        }

    @ConsistentCopyVisibility
    @OptIn(ExperimentalRaiseAccumulateApi::class)
    private data class ShowBalanceRequest private constructor(
        val lender: User,
        val borrower: User,
    ) {
        companion object {
            fun from(lenderId: Long): Either<NonEmptyList<RegisterBillValidationError>, ShowBalanceRequest> =
                User.of(id = lenderId)
                    .let {
                        either {
                            accumulate {
                                validateLender(it).bindNelOrAccumulate()
                            }
                            ShowBalanceRequest(
                                lender = it,
                                borrower = if (it == User.GARA) User.YUKI else User.GARA,
                            )
                        }
                    }
        }

        fun toParam() = ShowBalanceParam(
            lender = lender.name,
            borrower = borrower.name,
        )
    }
    private fun NonEmptyList<RegisterBillValidationError>.toEmbedBuilder(): EmbedBuilder.() -> Unit = {
        title = "Bad Request だっピ"
        color = Color(255, 0, 0)
        this@toEmbedBuilder.forEach {
            field(name = it.dataPath, inline = true, value = { it.message })
        }
    }
}
