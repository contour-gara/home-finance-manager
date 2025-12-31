package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe

class AmountTest : FunSpec({
    test("Amount の加算ができる") {
        // setup
        val amount1 = Amount(value = 1000)
        val amount2 = Amount(value = 500)

        // execute
        val actual = amount1 + amount2

        // assert
        val expected = Amount(value = 1500)
        actual shouldBe expected
    }

    context("Amount のファクトリーメソッドの動作確認") {
        test("正の値の場合、right に Amount が返る") {
            // execute
            val actual = Amount.of(value = 1)

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe Amount(value = 1)
            }
        }

        data class InvalidIntTestCase(val value: Int) : WithDataTestName {
            override fun dataTestName(): String = "入力が $value の場合、ValidationError を返す"
        }

        withData(
            InvalidIntTestCase(value = 0),
            InvalidIntTestCase(value = -1),
        ) { (value) ->
            // execute
            val actual = Amount.of(value = value)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "amount",
                    invalidValue = value,
                    detail = "amount must be positive.",
                )
            }
        }
    }
})
