package org.contourgara.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import org.contourgara.DiscordBotConfig
import org.contourgara.domain.UlidGenerator
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory
import ulid.ULID

@Single
class UlidGeneratorImpl(private val discordBotConfig: DiscordBotConfig) : UlidGenerator {
    private val httpClient: HttpClient by lazy {
        HttpClient(engineFactory = CIO) {
            install(plugin = Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        LoggerFactory.getLogger(HttpClient::class.java).debug(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                url(urlString = discordBotConfig.ulidSequencerBaseUrl)
            }
        }
    }

    override fun nextUlid(): ULID =
        runBlocking {
            httpClient
                .get(urlString = "/next-ulid")
                .bodyAsText()
                .let { ULID.parseULID(ulidString = it) }
        }
}
