package org.contourgara

import org.koin.core.annotation.Property
import org.koin.core.annotation.PropertyValue
import org.koin.core.annotation.Single

@Single
data class DiscordBotConfig(
    @Property("HOME_FINANCE_MANAGER_BOT_TOKEN")
    val homeFinanceManagerBotToken: String,
    @Property("HOME_FINANCE_MANAGER_CHANNEL_ID")
    val homeFinanceManagerBotChannelId: String
) {
    companion object {
        @PropertyValue("HOME_FINANCE_MANAGER_CHANNEL_ID")
        const val HOME_FINANCE_MANAGER_CHANNEL_ID = "1402331708459581591"
    }
}
