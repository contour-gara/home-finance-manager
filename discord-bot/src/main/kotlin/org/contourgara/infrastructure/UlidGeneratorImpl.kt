package org.contourgara.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.contourgara.DiscordBotConfig
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single
import ulid.ULID

@Single
class UlidGeneratorImpl(private val discordBotConfig: DiscordBotConfig) : UlidGenerator {
    override fun nextUlid(): ULID = runBlocking {
        HttpClient(CIO)
            .get("${discordBotConfig.ulidSequencerBaseUrl}/next-ulid")
            .body<String>()
            .let { ULID.parseULID(it) }
    }
}
