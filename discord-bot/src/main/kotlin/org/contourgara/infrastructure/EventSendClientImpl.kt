package org.contourgara.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.contourgara.DiscordBotConfig
import org.contourgara.domain.Bill
import org.contourgara.domain.EventSendClient
import org.koin.core.annotation.Single

@Single
class EventSendClientImpl(
    private val discordBotConfig: DiscordBotConfig,
) : EventSendClient {
    override fun registerBill(bill: Bill) {
        runBlocking {
            HttpClient(CIO) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }

                install(ContentNegotiation) {
                    json()
                }
            }
                .use { client ->
                    client.post("${discordBotConfig.kafkaRestProxyBaseUrl}/v3/clusters/${discordBotConfig.kafkaClusterId}/topics/${discordBotConfig.registerBillTopicName}/records") {
                        contentType(ContentType.Application.Json)
                        setBody(RegisterBillRequest.from(bill))
                    }
                }
                .also {
                    if (!it.status.isSuccess()) throw RuntimeException("Bad Request")
                }
                .body<ProduceRecordResponse>()
                .also {
                    if (it.isFailure()) throw RuntimeException("Bad Request")
                }
                .let { Unit }
        }
    }

    @Serializable
    data class RegisterBillRequest(
        private val value: RegisterBillValue,
    ) {
        companion object {
            fun from(bill: Bill) = RegisterBillRequest(
                value = RegisterBillValue.from(bill)
            )
        }
    }

    @Serializable
    data class RegisterBillValue(
        private val type: String = "JSON",
        private val data: RegisterBillValueData,
    ) {
        companion object {
            fun from(bill: Bill) = RegisterBillValue(
                data = RegisterBillValueData.from(bill)
            )
        }
    }

    @Serializable
    data class RegisterBillValueData(
        private val billId: String,
        private val amount: Int,
        private val lender: String,
        private val borrower: String,
        private val memo: String,
    ) {
        companion object {
            fun from(bill: Bill) = RegisterBillValueData(
                billId = bill.billId.toString(),
                amount = bill.amount,
                lender = bill.lender.name,
                borrower = bill.borrower.name,
                memo = bill.memo,
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class ProduceRecordResponse(
        @SerialName("error_code")
        private val errorCode: Int,
    ) {
        fun isFailure() = errorCode != 200
    }
}
