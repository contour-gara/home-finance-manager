package org.contourgara.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.extensions.wiremock.ListenerMode
import io.kotest.extensions.wiremock.WireMockListener
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.contourgara.DiscordBotConfig
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import ulid.ULID

class UlidGeneratorImplTest : KoinTest, FunSpec() {
    init {
        val wireMockServer = WireMockServer(28080)

        extensions(
            KoinExtension(org_contourgara_DiscordBotModule) { mockk<DiscordBotConfig>() },
            WireMockListener(wireMockServer, ListenerMode.PER_SPEC)
        )

        context("ulid-sequencer から ULID を取得できる") {
            data class TestCase(val ulid: String) : WithDataTestName {
                override fun dataTestName(): String = "ulid-sequencer から $ulid が返る場合"
            }

            withData(
                TestCase("01K4MXEKC0PMTJ8FA055N4SH78"),
                TestCase("01K4MXEKC0PMTJ8FA055N4SH79"),
            ) { (ulid) ->
                test("同じ ULID を返す") {
                    // setup
                    declareMock<DiscordBotConfig> {
                        every { ulidSequencerBaseUrl } returns "http://localhost:28080"
                    }

                    val sut: UlidGeneratorImpl by inject()

                    wireMockServer.stubFor(
                        get(urlPathEqualTo("/next-ulid"))
                            .willReturn(ok(ulid))
                    )

                    // execute
                    val actual = sut.nextUlid()

                    // assert
                    val expected = ULID.parseULID(ulid)
                    actual shouldBe expected
                }
            }
        }
    }
}
