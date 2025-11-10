package org.contourgara.infrastructure

import org.contourgara.FinanceCoreConfig
import org.contourgara.domain.UlidGenerator
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import ulid.ULID

@Component
class UlidGeneratorImpl(
    private val financeCoreConfig: FinanceCoreConfig,
) : UlidGenerator {
    val restClient:  RestClient by lazy {
        RestClient
            .builder()
            .baseUrl(financeCoreConfig.ulidGeneratorBaseUrl)
            .build()
    }

    override fun nextUlid(): ULID =
        restClient
            .get()
            .uri("/next-ulid")
            .retrieve()
            .body(String::class.java)
            .let { ULID.parseULID(it) }
}
