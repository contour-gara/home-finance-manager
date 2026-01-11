package org.contourgara.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.contourgara.DiscordBotConfig
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseClient
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory
import ulid.ULID

@Single
class ExpenseClientImpl(
    private val discordBotConfig: DiscordBotConfig,
) : ExpenseClient {
    override fun create(expense: Expense): Pair<Expense, ExpenseEventId> =
        runBlocking {
            HttpClient(CIO) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            LoggerFactory.getLogger(HttpClient::class.java).debug(message)
                        }
                    }
                    level = LogLevel.ALL
                }

                install(ContentNegotiation) {
                    json(
                        Json
                    )
                }
            }
                .use { client ->
                    client.post(urlString = "${discordBotConfig.expensesApiBaseUrl}/expense") {
                        contentType(type = ContentType.Application.Json)
                        setBody(body = CreateExpenseRequest.from(expense = expense))
                    }
                }
                .also {
                    if (!it.status.isSuccess()) throw RuntimeException("Bad Request")
                }
                .body<CreateExpenseResponse>()
                .let {
                    Pair(
                        first = expense,
                        second = ExpenseEventId(
                            value = ULID
                                .parseULID(
                                    ulidString = it.expenseEventId
                                )
                        )
                    )
                }
        }

    override fun delete(expenseId: ExpenseId): Pair<ExpenseId, ExpenseEventId> =
        runBlocking {
            HttpClient(engineFactory = CIO) {
                install(plugin = Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            LoggerFactory.getLogger(HttpClient::class.java).debug(message)
                        }
                    }
                    level = LogLevel.ALL
                }
                install(plugin = ContentNegotiation) {
                    json()
                }
            }
                .use { client ->
                    client.delete(urlString = "${discordBotConfig.expensesApiBaseUrl}/expense/${expenseId.value}")
                }
                .also {
                    if (!it.status.isSuccess()) throw RuntimeException("Bad Request")
                }
                .body<DeleteExpenseResponse>()
                .let {
                    Pair(
                        first = expenseId,
                        second = ExpenseEventId(value = ULID.parseULID(ulidString = it.expenseEventId)),
                    )
                }
        }
}

@Serializable
data class CreateExpenseRequest(
    val expenseId: String,
    val amount: Int,
    val category: String,
    val payer: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    companion object {
        fun from(expense: Expense): CreateExpenseRequest =
            CreateExpenseRequest(
                expenseId = expense.expenseId.value.toString(),
                amount = expense.amount,
                category = expense.category,
                payer = expense.payer,
                year = expense.year,
                month = expense.month,
                memo = expense.memo,
            )
    }
}

@Serializable
data class CreateExpenseResponse(
    val expenseId: String,
    val expenseEventId: String,
)

@Serializable
data class DeleteExpenseResponse(
    val expenseEventId: String,
)
