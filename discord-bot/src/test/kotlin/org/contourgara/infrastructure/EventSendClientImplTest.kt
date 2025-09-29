package org.contourgara.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
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
import org.contourgara.domain.Bill
import org.contourgara.domain.BillOperation
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import ulid.ULID
import wiremock.com.google.common.net.HttpHeaders

class EventSendClientImplTest : KoinTest, FunSpec() {
    init {
        val wireMockServer = WireMockServer(28080)

        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) { mockkClass(it) },
            WireMockListener(wireMockServer, ListenerMode.PER_SPEC),
        )

        test("トピックにメッセージを送信できる") {
            // setup
            declareMock<DiscordBotConfig> {
                every { kafkaRestProxyBaseUrl } returns "http://localhost:28080"
                every { kafkaClusterId } returns "home-finance-manager-kafka"
                every { kafkaTopicName } returns "home-finance-manager-topic"
            }

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics/home-finance-manager-topic/records"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo("{\"headers\":[{\"name\":\"billOperation\",\"value\":[82,69,71,73,83,84,69,82]},{\"name\":\"billId\",\"value\":[48,49,75,54,55,84,67,54,83,48,57,74,82,51,69,51,48,53,75,55,83,81,81,48,54,66]}],\"value\":{\"type\":\"JSON\",\"data\":{\"billId\":\"01K67TC6S09JR3E305K7SQQ06B\",\"amount\":1,\"lender\":\"GARA\",\"borrower\":\"YUKI\",\"memo\":\"memo\"}}}"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"error_code\":200,\"cluster_id\":\"home_finance_manager_kafka\",\"topic_name\":\"test-topic\",\"partition_id\":0,\"offset\":0,\"timestamp\":\"2025-09-28T22:54:48.379Z\",\"value\":{\"type\":\"JSON\",\"size\":98}}")
                    )
            )

            val ulid = ULID.parseULID("01K67TC6S09JR3E305K7SQQ06B")

            val sut: EventSendClientImpl by inject()

            // execute & assert
            shouldNotThrowAny {
                sut.execute(
                    BillOperation.REGISTER,
                    Bill.of(ulid, 1, "gara", "yuki", "memo")
                )
            }
        }

        test("トピックにメッセージを送信でリクエストに失敗した場合、例外を投げる") {
            // setup
            declareMock<DiscordBotConfig> {
                every { kafkaRestProxyBaseUrl } returns "http://localhost:28080"
                every { kafkaClusterId } returns "home-finance-manager-kafka"
                every { kafkaTopicName } returns "home-finance-manager-topic"
            }

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics/home-finance-manager-topic/records"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo("{\"headers\":[{\"name\":\"billOperation\",\"value\":[82,69,71,73,83,84,69,82]},{\"name\":\"billId\",\"value\":[48,49,75,54,55,84,67,54,83,48,57,74,82,51,69,51,48,53,75,55,83,81,81,48,54,66]}],\"value\":{\"type\":\"JSON\",\"data\":{\"billId\":\"01K67TC6S09JR3E305K7SQQ06B\",\"amount\":1,\"lender\":\"GARA\",\"borrower\":\"YUKI\",\"memo\":\"memo\"}}}"))
                    .willReturn(
                        aResponse()
                            .withStatus(405)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    )
            )

            val ulid = ULID.parseULID("01K67TC6S09JR3E305K7SQQ06B")

            val sut: EventSendClientImpl by inject()

            // execute & assert
            shouldThrowExactly<RuntimeException> {
                sut.execute(
                    BillOperation.REGISTER,
                    Bill.of(ulid, 1, "gara", "yuki", "memo")
                )
            }.message shouldBe "Bad Request"
        }

        test("トピックにメッセージを送信でリクエストに成功したが、200 以外の error_code が返却された場合、例外を投げる") {
            // setup
            declareMock<DiscordBotConfig> {
                every { kafkaRestProxyBaseUrl } returns "http://localhost:28080"
                every { kafkaClusterId } returns "home-finance-manager-kafka"
                every { kafkaTopicName } returns "home-finance-manager-topic"
            }

            wireMockServer.stubFor(
                post(urlPathEqualTo("/v3/clusters/home-finance-manager-kafka/topics/home-finance-manager-topic/records"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo("{\"headers\":[{\"name\":\"billOperation\",\"value\":[82,69,71,73,83,84,69,82]},{\"name\":\"billId\",\"value\":[48,49,75,54,55,84,67,54,83,48,57,74,82,51,69,51,48,53,75,55,83,81,81,48,54,66]}],\"value\":{\"type\":\"JSON\",\"data\":{\"billId\":\"01K67TC6S09JR3E305K7SQQ06B\",\"amount\":1,\"lender\":\"GARA\",\"borrower\":\"YUKI\",\"memo\":\"memo\"}}}"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"error_code\":400,\"message\":\"Cannot deserialize value of type `byte[]` from String \\\"\\\": Unexpected end of base64-encoded String: base64 variant 'MIME-NO-LINEFEEDS' expects padding (one or more '=' characters) at the end. This Base64Variant might have been incorrectly configured\"}")
                    )
            )

            val ulid = ULID.parseULID("01K67TC6S09JR3E305K7SQQ06B")

            val sut: EventSendClientImpl by inject()

            // execute & assert
            shouldThrowExactly<RuntimeException> {
                sut.execute(
                    BillOperation.REGISTER,
                    Bill.of(ulid, 1, "gara", "yuki", "memo")
                )
            }.message shouldBe "Bad Request"
        }

        xtest("リクエストシリアライズ確認用") {
            val sut = EventSendClientImpl.ProduceRecordRequest.from(
                BillOperation.REGISTER,
                Bill.of(ULID.nextULID(), 1, "gara", "yuki", "memo")
            )
            val json = Json {
                encodeDefaults = true
            }
            println(json.encodeToString(sut))
        }
    }
}
