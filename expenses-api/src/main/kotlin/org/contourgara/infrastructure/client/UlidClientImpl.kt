package org.contourgara.infrastructure.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import org.contourgara.AppConfig
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.infrastructure.UlidClient
import ulid.ULID

class UlidClientImpl(
    private val appConfig: AppConfig,
) : UlidClient {
    override fun nextUlid(): ExpenseEventId =
        runBlocking {
            HttpClient(CIO) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }.use {
                it.get("${appConfig.ulidSequencerBaseUrl}/next-ulid")
                    .bodyAsText()
                    .let { ULID.parseULID(it) }
                    .let { ExpenseEventId(id = it) }
            }
        }
}
