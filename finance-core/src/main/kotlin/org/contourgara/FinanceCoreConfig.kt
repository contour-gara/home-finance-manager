package org.contourgara

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "finance-core")
class FinanceCoreConfig {
    var ulidGeneratorBaseUrl: String = ""
}
