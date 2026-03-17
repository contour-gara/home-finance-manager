package org.contourgara.infrastructure.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import org.contourgara.AppConfig
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.infrastructure.IdClient
import org.slf4j.LoggerFactory
import ulid.ULID

class IdClientImpl(
    private val appConfig: AppConfig,
) : IdClient {
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
                url(urlString = appConfig.ulidSequencerBaseUrl)
            }
        }
    }

    override fun nextExpensesId(): ExpenseId =
        runBlocking {
            httpClient
                .get(urlString = "/next-ulid")
                .bodyAsText()
                .let { ULID.parseULID(it) }
                .let { ExpenseId(value = it) }
        }

    override fun nextExpensesEventId(): ExpenseEventId =
        runBlocking {
            httpClient
                .get(urlString = "/next-ulid")
                    .bodyAsText()
                    .let { ULID.parseULID(it) }
                    .let { ExpenseEventId(value = it) }
        }
}
