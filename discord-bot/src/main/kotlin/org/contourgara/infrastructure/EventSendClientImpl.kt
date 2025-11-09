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
import org.contourgara.domain.BillOperation
import org.contourgara.domain.EventSendClient
import org.koin.core.annotation.Single

@Single
class EventSendClientImpl(
    private val discordBotConfig: DiscordBotConfig,
) : EventSendClient {
    override fun execute(billOperation: BillOperation, bill: Bill) =
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
                    client.post("${discordBotConfig.kafkaRestProxyBaseUrl}/v3/clusters/${discordBotConfig.kafkaClusterId}/topics/${discordBotConfig.kafkaTopicName}/records") {
                        contentType(ContentType.Application.Json)
                        setBody(ProduceRecordRequest.from(billOperation, bill))
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

    @Serializable
    data class ProduceRecordRequest(
        private val headers: List<ProduceRecordHeader>,
        private val value: ProduceRecordValue,
    ) {
        companion object {
            fun from(billOperation: BillOperation, bill: Bill) = ProduceRecordRequest(
                headers = listOf(
                    ProduceRecordHeader.from(billOperation),
                    ProduceRecordHeader.from(bill),
                ),
                value = ProduceRecordValue(
                    data = ProduceRecordValueData.from(bill)
                )
            )
        }
    }

    @Serializable
    data class ProduceRecordHeader(
        private val name: String,
        private val value: ByteArray,
    ) {
        companion object {
            fun from(billOperation: BillOperation) = ProduceRecordHeader(
                name = "billOperation",
                value = billOperation.name.toByteArray()
            )

            fun from(bill: Bill) = ProduceRecordHeader(
                name = "billId",
                value = bill.billId.toString().toByteArray()
            )
        }
    }

    @Serializable
    data class ProduceRecordValue(
        private val type: String = "JSON",
        private val data: ProduceRecordValueData,
    )

    @Serializable
    data class ProduceRecordValueData(
        private val billId: String,
        private val amount: Int,
        private val lender: String,
        private val borrower: String,
        private val memo: String,
    ) {
        companion object {
            fun from(bill: Bill) = ProduceRecordValueData(
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
