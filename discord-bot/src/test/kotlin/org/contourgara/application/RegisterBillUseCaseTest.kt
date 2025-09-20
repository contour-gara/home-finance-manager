package org.contourgara.application

import io.kotest.core.spec.style.StringSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.contourgara.domain.UlidGenerator
import org.koin.ksp.generated.org_contourgara_DiscordBotModule
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import ulid.ULID

class RegisterBillUseCaseTest : KoinTest, StringSpec() {
    init {
        extensions(KoinExtension(org_contourgara_DiscordBotModule) { mockk<UlidGenerator>() })

        "生成される ULID が 01K5EZVS4SQ695EMPX61GM7KHW の場合" {
            // setup
            declareMock<UlidGenerator> {
                every { generate() } returns ULID.parseULID("01K5EZVS4SQ695EMPX61GM7KHW")
            }

            val sut:RegisterBillUseCase by inject()

            val registerBillParam = RegisterBillParam(1, "yuki", "gara", "memo")

            // execute
            val actual = sut.execute(registerBillParam)

            // assert
            val expected = RegisterBillDto("01K5EZVS4SQ695EMPX61GM7KHW", 1, "yuki", "gara", "memo")
            actual shouldBe expected
        }

        "生成される ULID が 01K5C11Z3TPPZ5H95MMTQV77RP の場合" {
            // setup
            declareMock<UlidGenerator> {
                every { generate() } returns ULID.parseULID("01K5C11Z3TPPZ5H95MMTQV77RP")
            }

            val sut:RegisterBillUseCase by inject()

            val registerBillParam = RegisterBillParam(1, "yuki", "gara", "memo")

            // execute
            val actual = sut.execute(registerBillParam)

            // assert
            val expected = RegisterBillDto("01K5C11Z3TPPZ5H95MMTQV77RP", 1, "yuki", "gara", "memo")
            actual shouldBe expected
        }
    }
}
