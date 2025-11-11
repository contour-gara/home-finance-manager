package org.contourgara

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "finance-core")
class FinanceCoreConfig {
    var ulidGeneratorBaseUrl: String = ""
    var discordBotToken: String = ""
    var discordChannelId: String = "1402331708459581591"
}
