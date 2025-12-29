package org.contourgara.infrastructure.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.contourgara.AppConfig
import org.contourgara.domain.ExpenseEventId
import ulid.ULID

class UlidClientImplTest : FunSpec({
    val wireMockServer = WireMockServer(28080)

    extensions(
        WireMockListener(wireMockServer, ListenerMode.PER_SPEC),
    )

    context("ulid-sequencer から ULID を取得できる") {
        data class TestCase(val ulid: String) : WithDataTestName {
            override fun dataTestName(): String = "ulid-sequencer から $ulid が返る場合"
        }

        withData(
            TestCase("01KD27JEZQQY88RG18034YZHBV"),
            TestCase("01KDHVD5XTTR9XR4ZAFSSETGXS"),
        ) { (ulid) ->
            test("同じ ULID を返す") {
                // setup
                val appConfig = mockk<AppConfig>()
                every { appConfig.ulidSequencerBaseUrl } returns "http://localhost:28080"

                val sut = UlidClientImpl(appConfig = appConfig)

                wireMockServer.stubFor(
                    get(urlPathEqualTo("/next-ulid"))
                        .willReturn(ok(ulid))
                )

                // execute
                val actual = sut.nextUlid()

                // assert
                val expected = ExpenseEventId(id = ULID.parseULID(ulid))
                actual shouldBe expected
            }
        }
    }
})
