package org.contourgara.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.delete
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkClass
import org.contourgara.DiscordBotConfig
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import ulid.ULID
import wiremock.com.google.common.net.HttpHeaders
import kotlin.getValue

class ExpenseClientImplTest : KoinTest, FunSpec() {
    init {
        val wireMockServer = WireMockServer(28080)
        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) { mockkClass(type = it) },
            WireMockListener(wireMockServer, ListenerMode.PER_SPEC),
        )

        test(name = "支出作成エンドポイントにリクエストを行い、支出と支出イベント ID を返す") {
            // setup
            declareMock<DiscordBotConfig> {
                every { expensesApiBaseUrl } returns "http://localhost:28080"
            }

            wireMockServer.stubFor(
                post(urlPathEqualTo("/expense"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo("{\"expenseId\":\"01K5EZVS4SQ695EMPX61GM7KHW\",\"amount\":1000,\"category\":\"FOOD\",\"payer\":\"YUKI\",\"year\":2026,\"month\":1,\"memo\":\"memo\"}"))
                    .willReturn(
                        aResponse()
                            .withStatus(201)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"expenseId\":\"01K5EZVS4SQ695EMPX61GM7KHW\",\"expenseEventId\":\"01KD27JEZQQY88RG18034YZHBV\"}")
                    )
            )

            val expense = Expense(
                expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW")),
                amount = 1000,
                category = "FOOD",
                payer = "YUKI",
                year = 2026,
                month = 1,
                memo = "memo",
            )

            val sut: ExpenseClientImpl by inject()

            // execute
            val actual = sut.create(expense = expense)

            // assert
            val expected = Pair(
                first = expense,
                second = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
            )
            actual shouldBe expected
        }

        test(name = "支出作成エンドポイントにリクエストを行いエラーが発生した場合、例外を投げる") {
            // setup
            declareMock<DiscordBotConfig> {
                every { expensesApiBaseUrl } returns "http://localhost:28080"
            }

            wireMockServer.stubFor(
                post(urlPathEqualTo("/expense"))
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                    .withRequestBody(equalTo("{\"expenseId\":\"01K5EZVS4SQ695EMPX61GM7KHW\",\"amount\":1000,\"category\":\"FOOD\",\"payer\":\"YUKI\",\"year\":2026,\"month\":1,\"memo\":\"memo\"}"))
                    .willReturn(
                        aResponse()
                            .withStatus(400)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"type\":\"org.contourgara.domain.ValidationException\",\"title\":\"Validation Error\",\"errors\":[{\"detail\":\"value is not supported.: year = 2025\"}]}")
                    )
            )

            val expense = Expense(
                expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW")),
                amount = 1000,
                category = "FOOD",
                payer = "YUKI",
                year = 2025,
                month = 1,
                memo = "memo",
            )

            val sut: ExpenseClientImpl by inject()

            // execute & assert
            shouldThrowExactly<RuntimeException> {
                sut.create(expense = expense)
            }.message shouldBe "Bad Request"
        }

        test(name = "支出削除エンドポイントにリクエストを行い、支出 ID と支出イベント ID を返す") {
            // setup
            declareMock<DiscordBotConfig> {
                every { expensesApiBaseUrl } returns "http://localhost:28080"
            }

            wireMockServer.stubFor(
                delete(urlPathEqualTo("/expense/01K5EZVS4SQ695EMPX61GM7KHW"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"expenseEventId\":\"01KD27JEZQQY88RG18034YZHBV\"}")
                    )
            )

            val expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW"))

            val sut: ExpenseClientImpl by inject()

            // execute
            val actual = sut.delete(expenseId = expenseId)

            // assert
            val expected = Pair(
                first = expenseId,
                second = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
            )
            actual shouldBe expected
        }

        test(name = "支出削除エンドポイントにリクエストを行い支出が存在しない場合、例外を投げる") {
            // setup
            declareMock<DiscordBotConfig> {
                every { expensesApiBaseUrl } returns "http://localhost:28080"
            }

            wireMockServer.stubFor(
                delete(urlPathEqualTo("/expense/01K5EZVS4SQ695EMPX61GM7KHW"))
                    .willReturn(
                        aResponse()
                            .withStatus(404)
                            .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .withBody("{\"type\":\"org.contourgara.domain.ExpenseNotFoundException\",\"title\":\"Expense was not found\",\"errors\":[{\"detail\":\"The expense with the given ID was not found.: expenseId = 01K4MXEKC0PMTJ8FA055N4SH79\"}]}")
                    )
            )

            val expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW"))

            val sut: ExpenseClientImpl by inject()

            // execute & assert
            shouldThrowExactly<RuntimeException> {
                sut.delete(expenseId = expenseId)
            }.message shouldBe "Bad Request"
        }
    }
}
