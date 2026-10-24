package org.contourgara

import org.contourgara.eventlistener.DiscordEventListener
import org.contourgara.infrastructure.KafkaInit
import org.koin.core.annotation.KoinApplication
import org.koin.environmentProperties
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
object DiscordBotApplication

suspend fun main() {
    startKoin<DiscordBotApplication> {
        printLogger()
        environmentProperties()
    }

    KafkaInit.execute()
    DiscordEventListener.start()
}
