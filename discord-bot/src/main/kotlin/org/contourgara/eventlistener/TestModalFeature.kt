package org.contourgara.eventlistener

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent

object TestModalFeature {
    const val TEST_MODAL_COMMAND_NAME = "modal"
    const val TEST_MODAL_COMMAND_DESCRIPTION = "modal test"
    const val TEST_MODAL_MODAL_ID = "modal"
    private const val TEST_MODAL_MODAL_TEXT_INPUT_ID = "memo"

    suspend fun GuildChatInputCommandInteractionCreateEvent.openTestModal() = when (interaction.channel.id) {
        Snowflake(1402331708459581591) -> interaction.modal("テストモーダル", TEST_MODAL_MODAL_ID) {
            actionRow {
                textInput(TextInputStyle.Short, TEST_MODAL_MODAL_TEXT_INPUT_ID, "メモ") {
                    placeholder = "メモを入力"
                    allowedLength = 1..100
                }
            }
        }
        else -> interaction.deferPublicResponse().respond { content = "test で実行してね" }
    }

    suspend fun ModalSubmitInteractionCreateEvent.submitTestModal() {
        val memo = interaction.textInputs[TEST_MODAL_MODAL_TEXT_INPUT_ID]?.value
        println(memo)
        interaction.deferPublicResponse().respond { content = "受け付けたっぴ: $memo" }
    }
}
