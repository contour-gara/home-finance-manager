package org.contourgara.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.contourgara.DiscordBotConfig
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single
import ulid.ULID

@Single
class UlidGeneratorImpl(private val discordBotConfig: DiscordBotConfig) : UlidGenerator {
    override fun nextUlid(): ULID = runBlocking {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }.use {
            it.get("${discordBotConfig.ulidSequencerBaseUrl}/next-ulid")
                .body<String>()
                .let { ULID.parseULID(it) }
        }
    }
}
