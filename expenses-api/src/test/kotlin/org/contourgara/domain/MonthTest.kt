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

class MonthTest : FunSpec({
    test("Int から Month を取得できる") {
        // execute
        val actual = Month.of(value = 1)

        // assert
        val expected = Month.JANUARY
        actual shouldBe expected
    }

    test("Int から Month 取得で不適切な値の場合は例外が投げられる") {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Month.of(value = 13)
        }.message shouldBe "Not found: 13"
    }

    context("Month のファクトリーメソッドの動作確認") {
        test("サポートしている月の場合、right に Month が返る") {
            // execute
            val actual = Month.ofValidate(value = 1)

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe Month.of(value = 1)
            }
        }

        data class InvalidMonthTestCase(val value: Int) : WithDataTestName {
            override fun dataTestName(): String = "入力が $value の場合、ValidationError を返す"
        }

        withData(
            InvalidMonthTestCase(value = 0),
            InvalidMonthTestCase(value = 13),
        ) { (value) ->
            // execute
            val actual = Month.ofValidate(value = value)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "month",
                    invalidValue = value,
                    detail = "value is not supported.",
                )
            }
        }
    }
})
