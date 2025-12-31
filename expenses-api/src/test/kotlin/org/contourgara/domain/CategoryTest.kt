package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe

class CategoryTest : FunSpec({
    context("Category のファクトリーメソッドの動作確認") {
        test("サポートしているカテゴリの場合、right に Category が返る") {
            // execute
            val actual = Category.of(value = "FOOD")

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe Category.FOOD
            }
        }

        data class InvalidCategoryTestCase(val value: String) : WithDataTestName {
            override fun dataTestName(): String = "入力が $value の場合、ValidationError を返す"
        }

        withData(
            InvalidCategoryTestCase(value = "a"),
            InvalidCategoryTestCase(value = ""),
        ) { (value) ->
            // execute
            val actual = Category.of(value = value)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "category",
                    invalidValue = value,
                    detail = "value is not supported.",
                )
            }
        }
    }
})
