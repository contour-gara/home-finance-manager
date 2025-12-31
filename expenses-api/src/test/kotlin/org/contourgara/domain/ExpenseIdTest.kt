package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe
import ulid.ULID

class ExpenseIdTest : FunSpec({
    context("ExpenseId のファクトリーメソッドの動作確認") {
        test("ULID にパースできる場合、right に ExpenseId が返る") {
            // execute
            val actual = ExpenseId.of(id = "01K4MXEKC0PMTJ8FA055N4SH79")

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe ExpenseId(id = ULID.parseULID("01K4MXEKC0PMTJ8FA055N4SH79"))
            }
        }

        data class InvalidUlidTestCase(val id: String) : WithDataTestName {
            override fun dataTestName(): String = "入力が $id の場合、ValidationError を返す"
        }

        withData(
            InvalidUlidTestCase(id = "test"),
            InvalidUlidTestCase(id = "aaaaaaaaaaaaaaaaaaaaaaaaaa"),
        ) { (id) ->
            // execute
            val actual = ExpenseId.of(id = id)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "expenseId",
                    invalidValue = id,
                    detail = "value is not a valid ULID format.",
                )
            }
        }
    }
})
