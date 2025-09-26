package org.contourgara

import org.contourgara.eventlistener.DiscordEventListener
import org.koin.core.annotation.KoinApplication
import org.koin.environmentProperties
import org.koin.ksp.generated.startKoin

@KoinApplication
object DiscordBotApplication

suspend fun main() {
    DiscordBotApplication.startKoin {
        printLogger()
        environmentProperties()
    }

    DiscordEventListener.start()
}
