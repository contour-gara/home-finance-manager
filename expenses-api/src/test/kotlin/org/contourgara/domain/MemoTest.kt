package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe

class MemoTest : FunSpec({
    context("Memo のファクトリーメソッドの動作確認") {
        test("空文字以外の場合、right に Memo が返る") {
            // execute
            val actual = Memo.of(value = "ランチ")

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe Memo(value = "ランチ")
            }
        }

        data class InvalidMemoTestCase(val value: String) : WithDataTestName {
            override fun dataTestName(): String = "入力が '$value' の場合、ValidationError を返す"
        }

        withData(
            InvalidMemoTestCase(value = ""),
            InvalidMemoTestCase(value = "　"),
        ) { (value) ->
            // execute
            val actual = Memo.of(value = value)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "memo",
                    invalidValue = value,
                    detail = "memo must not be blank.",
                )
            }
        }
    }
})
