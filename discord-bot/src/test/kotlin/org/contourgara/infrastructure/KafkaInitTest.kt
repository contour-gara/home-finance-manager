package org.contourgara.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.serialization.json.Json
import org.contourgara.DiscordBotConfig
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import wiremock.com.google.common.net.HttpHeaders

class KafkaInitTest : KoinTest, FunSpec() {
    init {
        val wireMockServer = WireMockServer(28080)

        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) { mockkClass(it) },
            WireMockListener(wireMockServer, ListenerMode.PER_SPEC),
        )

        test("トピックが存在しない場合、トピックを作成する") {
            // setup
            declareMock<DiscordBotConfig> {
                every { kafkaRestProxyBaseUrl } returns "http://localhost:28080"
                every { kafkaClusterId } returns "home-finance-manager-kafka"
                every { registerBillTopicName } returns "register-bill"
                every { deleteBillTopicName } returns "delete-bill"
            }

            wireMockServer.stubFor(
                get(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .willReturn(
                        ok()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody(Json.encodeToString(KafkaInit.GetAllTopicsResponse(emptyList())))
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("register-bill"))))
                    .willReturn(
                        aResponse()
                            .withStatus(201)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("delete-bill"))))
                    .willReturn(
                        aResponse()
                            .withStatus(201)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    )
            )

            // execute & assert
            shouldNotThrowAny { KafkaInit.execute() }
        }

        test("トピック作成でエラーになる場合、例外を投げる") {
            // setup
            declareMock<DiscordBotConfig> {
                every { kafkaRestProxyBaseUrl } returns "http://localhost:28080"
                every { kafkaClusterId } returns "home-finance-manager-kafka"
                every { registerBillTopicName } returns "register-bill"
                every { deleteBillTopicName } returns "delete-bill"
            }

            wireMockServer.stubFor(
                get(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .willReturn(
                        ok()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody(Json.encodeToString(KafkaInit.GetAllTopicsResponse(emptyList())))
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("register-bill"))))
                    .willReturn(
                        aResponse()
                            .withStatus(400)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"error_code\":400,\"message\":\"Cannot construct instance of `CreateTopicRequest`, problem: Null topicName\"}")
                    )
            )

            // execute
            shouldThrowExactly<RuntimeException> { KafkaInit.execute() }
                .message shouldBe "Bad Request"
        }
    }
}
