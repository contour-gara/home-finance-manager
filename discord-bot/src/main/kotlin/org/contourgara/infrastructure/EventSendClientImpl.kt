package org.contourgara.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.contourgara.DiscordBotConfig
import org.contourgara.domain.Bill
import org.contourgara.domain.BillId
import org.contourgara.domain.EventSendClient
import org.contourgara.domain.User
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory

@Single
@OptIn(ExperimentalSerializationApi::class)
class EventSendClientImpl(
    private val discordBotConfig: DiscordBotConfig,
) : EventSendClient {
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
            install(plugin = ContentNegotiation) {
                json(
                    Json {
                        classDiscriminatorMode = ClassDiscriminatorMode.NONE
                        encodeDefaults = true
                    }
                )
            }
            defaultRequest {
                url(urlString = discordBotConfig.kafkaRestProxyBaseUrl)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    override fun registerBill(bill: Bill) {
        runBlocking {
            httpClient
                .post(urlString = "/v3/clusters/${discordBotConfig.kafkaClusterId}/topics/${discordBotConfig.registerBillTopicName}/records") {
                    setBody(RecordRequest.from(bill))
                }
                    .also {
                        if (!it.status.isSuccess()) throw RuntimeException("Bad Request")
                    }
                    .body<ProduceRecordResponse>()
                    .also {
                        if (it.isFailure()) throw RuntimeException("Bad Request")
                    }
        }
    }

    override fun deleteBill(billId: BillId) {
        runBlocking {
            httpClient
                .post(urlString = "/v3/clusters/${discordBotConfig.kafkaClusterId}/topics/${discordBotConfig.deleteBillTopicName}/records") {
                    contentType(ContentType.Application.Json)
                    setBody(RecordRequest.from(billId))
                }
                    .also {
                        if (!it.status.isSuccess()) throw RuntimeException("Bad Request")
                    }
                    .body<ProduceRecordResponse>()
                    .also {
                        if (it.isFailure()) throw RuntimeException("Bad Request")
                    }
        }
    }

    override fun showBalance(lender: User, borrower: User) {
        runBlocking {
            httpClient
                .post("${discordBotConfig.kafkaRestProxyBaseUrl}/v3/clusters/${discordBotConfig.kafkaClusterId}/topics/${discordBotConfig.showBalanceTopicName}/records") {
                        contentType(ContentType.Application.Json)
                        setBody(RecordRequest.from(lender, borrower))
                }
                    .also {
                        if (!it.status.isSuccess()) throw RuntimeException("Bad Request")
                    }
                    .body<ProduceRecordResponse>()
                    .also {
                        if (it.isFailure()) throw RuntimeException("Bad Request")
                    }
        }
    }

    @Serializable
    data class RecordRequest(
        private val value: RecordValue,
    ) {
        companion object {
            fun from(bill: Bill) = RecordRequest(
                value = RecordValue.from(bill)
            )

            fun from(billId: BillId) = RecordRequest(
                value = RecordValue.from(billId)
            )

            fun from(lender: User, borrower: User) = RecordRequest(
                value = RecordValue.from(lender, borrower)
            )
        }
    }

    @Serializable
    data class RecordValue(
        private val type: String = "JSON",
        private val data: RecordData,
    ) {
        companion object {
            fun from(bill: Bill) = RecordValue(
                data = RecordData.RegisterBillData.from(bill)
            )

            fun from(billId: BillId) = RecordValue(
                data = RecordData.DeleteBillData.from(billId)
            )

            fun from(lender: User, borrower: User) = RecordValue(
                data = RecordData.ShowBalanceData.from(lender, borrower)
            )
        }
    }

    @Serializable
    sealed interface RecordData {
        @Serializable
        data class RegisterBillData(
            private val billId: String,
            private val amount: Int,
            private val lender: String,
            private val borrower: String,
            private val memo: String,
        ) : RecordData {
            companion object {
                fun from(bill: Bill) = RegisterBillData(
                    billId = bill.billId.value.toString(),
                    amount = bill.amount,
                    lender = bill.lender.name,
                    borrower = bill.borrower.name,
                    memo = bill.memo,
                )
            }
        }

        @Serializable
        data class DeleteBillData(
            private val billId: String,
        ) : RecordData {
            companion object {
                fun from(billId: BillId) = DeleteBillData(
                    billId = billId.value.toString(),
                )
            }
        }

        @Serializable
        data class ShowBalanceData(
            private val lender: String,
            private val borrower: String,
        ) : RecordData {
            companion object {
                fun from(lender: User, borrower: User) =
                    ShowBalanceData(
                        lender = lender.name,
                        borrower = borrower.name,
                    )
            }
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
