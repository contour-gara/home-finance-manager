package org.contourgara

import org.koin.core.annotation.Property
import org.koin.core.annotation.PropertyValue
import org.koin.core.annotation.Single

@Single
data class DiscordBotConfig(
    @Property("BOT_TOKEN")
    val botToken: String,
    @Property("CHANNEL_ID")
    val channelId: String,
    @Property("ULID_SEQUENCER_BASE_URL")
    val ulidSequencerBaseUrl: String,
    @Property("KAFKA_REST_PROXY_BASE_URL")
    val kafkaRestProxyBaseUrl: String,
    @Property("KAFKA_CLUSTER_ID")
    val kafkaClusterId: String,
    @Property("KAFKA_TOPIC_NAME")
    val kafkaTopicName: String,
) {
    companion object {
        @PropertyValue("CHANNEL_ID")
        const val CHANNEL_ID = "1402331708459581591"
        @PropertyValue("KAFKA_CLUSTER_ID")
        const val KAFKA_CLUSTER_ID = "home-finance-manager-kafka"
        @PropertyValue("KAFKA_TOPIC_NAME")
        const val KAFKA_TOPIC_NAME = "home-finance-manager-topic"
    }
}
