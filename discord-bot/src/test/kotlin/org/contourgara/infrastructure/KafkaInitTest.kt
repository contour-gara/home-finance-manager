package org.contourgara.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.created
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.koin.KoinExtension
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
                every { showBalanceTopicName } returns "show-balance"
                every { expensesApiMessagingBridgeTopicName } returns "expenses-api-messaging-bridge"
            }

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("register-bill"))))
                    .willReturn(
                        created()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"topic_name\":\"register-bill\"}")
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("delete-bill"))))
                    .willReturn(
                        created()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"topic_name\":\"delete-bill\"}")
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("show-balance"))))
                    .willReturn(
                        created()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"topic_name\":\"show-balance\"}")
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("expenses-api-messaging-bridge"))))
                    .willReturn(
                        created()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"topic_name\":\"expenses-api-messaging-bridge\"}")
                    )
            )

            // execute & assert
            shouldNotThrowAny { KafkaInit.execute() }
        }

        test("トピックが存在する場合、400 が返るが例外を投げない") {
            // setup
            declareMock<DiscordBotConfig> {
                every { kafkaRestProxyBaseUrl } returns "http://localhost:28080"
                every { kafkaClusterId } returns "home-finance-manager-kafka"
                every { registerBillTopicName } returns "register-bill"
                every { deleteBillTopicName } returns "delete-bill"
                every { showBalanceTopicName } returns "show-balance"
                every { expensesApiMessagingBridgeTopicName } returns "expenses-api-messaging-bridge"
            }

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("register-bill"))))
                    .willReturn(
                        badRequest()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"error_code\":40002,\"message\":\"Topic 'register-bill' already exists.\"}")
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("delete-bill"))))
                    .willReturn(
                        badRequest()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"error_code\":40002,\"message\":\"Topic 'delete-bill' already exists.\"}")
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("show-balance"))))
                    .willReturn(
                        badRequest()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"error_code\":40002,\"message\":\"Topic 'show-balance' already exists.\"}")
                    )
            )

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo(Json.encodeToString(KafkaInit.CreateTopicRequest("expenses-api-messaging-bridge"))))
                    .willReturn(
                        badRequest()
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"error_code\":40002,\"message\":\"Topic 'expenses-api-messaging-bridge' already exists.\"}")
                    )
            )

            // execute & assert
            shouldNotThrowAny { KafkaInit.execute() }
        }
    }
}
