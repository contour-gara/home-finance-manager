package org.contourgara

import org.contourgara.eventlistener.DiscordEventListener
import org.koin.core.context.startKoin
import org.koin.environmentProperties
import org.koin.ksp.generated.module

suspend fun main() {
    startKoin {
        environmentProperties()
        modules(DiscordBotModule().module)
    }

    DiscordEventListener().start()
}
