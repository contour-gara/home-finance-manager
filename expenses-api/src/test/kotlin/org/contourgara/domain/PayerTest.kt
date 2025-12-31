package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe

class PayerTest : FunSpec({
    context("Payer のファクトリーメソッドの動作確認") {
        test("サポートしている支払い者の場合、right に Payer が返る") {
            // execute
            val actual = Payer.of(value = "DIRECT_DEBIT")

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe Payer.DIRECT_DEBIT
            }
        }

        data class InvalidPayerTestCase(val value: String) : WithDataTestName {
            override fun dataTestName(): String = "入力が $value の場合、ValidationError を返す"
        }

        withData(
            InvalidPayerTestCase(value = "a"),
            InvalidPayerTestCase(value = ""),
        ) { (value) ->
            // execute
            val actual = Payer.of(value = value)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "payer",
                    invalidValue = value,
                    detail = "value is not supported.",
                )
            }
        }
    }
})
