package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe
import ulid.ULID

class ULIDTest : FunSpec({
    test("学習用テスト: ULID のパース失敗確認 1") {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            ULID.parseULID("test")
        }.message shouldBe "ulid string must be exactly 26 chars long"
    }

    test("学習用テスト: ULID のパース失敗確認 2") {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            ULID.parseULID("aaaaaaaaaaaaaaaaaaaaaaaaaa")
        }.message shouldBe "ulid string must not exceed '7ZZZZZZZZZZZZZZZZZZZZZZZZZ'!"
    }

    context("ULID のファクトリーメソッドの動作確認") {
        test("ULID の場合、right に ULID が返る") {
            // execute
            val actual = ULID.of("01K4MXEKC0PMTJ8FA055N4SH79")

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79")
            }
        }

        data class InvalidUlidTestCase(val ulidString: String) : WithDataTestName {
            override fun dataTestName(): String = "ULID が $ulidString の場合、ValidationError を返す"
        }

        withData(
            InvalidUlidTestCase(ulidString = "test"),
            InvalidUlidTestCase(ulidString = "aaaaaaaaaaaaaaaaaaaaaaaaaa"),
        ) { (ulidString) ->
            // execute
            val actual = ULID.of(ulidString = ulidString)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "/ulid",
                    invalidValue = ulidString,
                    detail = "value is not a valid ULID format.",
                )
            }
        }
    }
})
