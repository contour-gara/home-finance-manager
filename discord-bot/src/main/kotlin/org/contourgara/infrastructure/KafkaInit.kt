package org.contourgara.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object KafkaInit : KoinComponent {
    private val discordBotConfig: DiscordBotConfig by inject()

    fun execute() = runBlocking {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json()
            }
        }.use { client ->
            client.get("${discordBotConfig.kafkaRestProxyBaseUrl}/v3/clusters/${discordBotConfig.kafkaClusterId}/topics")
                .body<GetAllTopicsResponse>()
                .toCreatTopics()
                .forEach {
                    client.post("${discordBotConfig.kafkaRestProxyBaseUrl}/v3/clusters/${discordBotConfig.kafkaClusterId}/topics") {
                        contentType(ContentType.Application.Json)
                        setBody(CreateTopicRequest(it.topicName))
                    }.also {
                        if (!it.status.isSuccess()) throw RuntimeException("Bad Request")
                    }
                }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class GetAllTopicsResponse(
        private val data: List<TopicDataResponse>,
    ) {
        fun toCreatTopics(): List<TopicType> =
            TopicType.entries.filterNot {
                data.map { it.toTopicType() }.contains(it)
            }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class TopicDataResponse(
        @SerialName("topic_name")
        private val topicName: String,
    ) {
        fun toTopicType(): TopicType = TopicType.of(topicName)
    }

    enum class TopicType(val topicName: String) {
        REGISTER_BILL(discordBotConfig.registerBillTopicName),
        DELETE_BILL(discordBotConfig.deleteBillTopicName),
        OFFSET_BALANCE(discordBotConfig.offsetBalanceTopicName),
        ;

        companion object {
            fun of(topicName: String) =
                entries.find { it.topicName == topicName }
                    ?: throw IllegalArgumentException("Unknown topic name: $topicName" )
        }
    }

    @Serializable
    data class CreateTopicRequest(
        @SerialName("topic_name")
        private val topicName: String,
    )
}
