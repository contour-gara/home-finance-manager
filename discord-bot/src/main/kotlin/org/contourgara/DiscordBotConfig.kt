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
) {
    companion object {
        @PropertyValue("CHANNEL_ID")
        const val CHANNEL_ID = "1402331708459581591"
    }
}
