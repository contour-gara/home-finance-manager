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
import io.kotest.matchers.shouldBe
import org.contourgara.ExpensesApiMessagingBridgeConfig
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseEventId
import org.contourgara.domain.ExpenseId
import ulid.ULID
import wiremock.com.google.common.net.HttpHeaders

class ExpenseClientImplTest : FunSpec({
    val wireMockServer = WireMockServer(28080)
    extensions(WireMockListener(wireMockServer, ListenerMode.PER_SPEC))

    test("支出作成エンドポイントにリクエストを行い、201 が返る場合、支出と支出イベント ID を返す") {
        // setup
        val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig(
            datasourceUrl = "test",
            datasourceUser = "test",
            datasourcePassword = "test",
            expensesApiBaseUrl = wireMockServer.baseUrl(),
            discordBotToken = "test",
            discordChannelId = "test",
            kafkaBootstrapServer = "test",
            consumerAutoOffsetReset = "test",
        )

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

        val expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW"))
        val expense = Expense(
            expenseId = expenseId,
            amount = 1000,
            category = "FOOD",
            payer = "YUKI",
            year = 2026,
            month = 1,
            memo = "memo",
        )

        val sut = ExpenseClientImpl(expensesApiMessagingBridgeConfig)

        // execute
        val actual = sut.create(expense = expense)

        // assert
        val expected = Pair(
            first = expenseId,
            second = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
        )
        actual shouldBe expected
    }

    test("支出削除エンドポイントにリクエストを行い、200 が返る場合、支出と支出イベント ID を返す") {
        // setup
        val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig(
            datasourceUrl = "test",
            datasourceUser = "test",
            datasourcePassword = "test",
            expensesApiBaseUrl = wireMockServer.baseUrl(),
            discordBotToken = "test",
            discordChannelId = "test",
            kafkaBootstrapServer = "test",
            consumerAutoOffsetReset = "test",
        )

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

        val sut = ExpenseClientImpl(expensesApiMessagingBridgeConfig)

        // execute
        val actual = sut.delete(expenseId = expenseId)

        // assert
        val expected = Pair(
            first = expenseId,
            second = ExpenseEventId(value = ULID.parseULID(ulidString = "01KD27JEZQQY88RG18034YZHBV")),
        )
        actual shouldBe expected
    }

    test("支出削除エンドポイントにリクエストを行い、404 が返る場合、例外を投げる") {
        // setup
        val expensesApiMessagingBridgeConfig = ExpensesApiMessagingBridgeConfig(
            datasourceUrl = "test",
            datasourceUser = "test",
            datasourcePassword = "test",
            expensesApiBaseUrl = wireMockServer.baseUrl(),
            discordBotToken = "test",
            discordChannelId = "test",
            kafkaBootstrapServer = "test",
            consumerAutoOffsetReset = "test",
        )

        wireMockServer.stubFor(
            delete(urlPathEqualTo("/expense/01K5EZVS4SQ695EMPX61GM7KHW"))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                )
        )

        val expenseId = ExpenseId(value = ULID.parseULID(ulidString = "01K5EZVS4SQ695EMPX61GM7KHW"))

        val sut = ExpenseClientImpl(expensesApiMessagingBridgeConfig)

        // execute & assert
        shouldThrowExactly<RuntimeException> {
            sut.delete(expenseId = expenseId)
        }.message shouldBe "Bad Request"
    }
})
