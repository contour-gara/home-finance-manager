package org.contourgara

import org.contourgara.eventlistener.DiscordEventListener

suspend fun main() {
    DiscordEventListener().start()
}
