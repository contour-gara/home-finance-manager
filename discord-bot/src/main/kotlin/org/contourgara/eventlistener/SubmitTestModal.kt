package org.contourgara.eventlistener

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent

suspend fun ModalSubmitInteractionCreateEvent.submitTestModal() {
    val memo = interaction.textInputs["memo"]?.value
    println(memo)
    interaction.deferPublicResponse().respond { content = "受け付けたっぴ: $memo" }
}
