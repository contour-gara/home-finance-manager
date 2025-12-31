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

class YearTest : FunSpec({
    test("Int から Year を取得できる") {
        // execute
        val actual = Year.of(value = 2026)

        // assert
        val expected = Year._2026
        actual shouldBe expected
    }

    test("Int から Year 取得で不適切な値の場合は例外が投げられる") {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Year.of(value = 2025)
        }.message shouldBe "Not found: 2025"
    }

    context("Year のファクトリーメソッドの動作確認") {
        test("ULID にパースできる場合、right に _root_ide_package_.org.contourgara.domain.Year が返る") {
            // execute
            val actual = Year.ofValidate(value = 2026)

            // assert
            assertSoftly {
                actual.shouldBeRight()
                actual.value shouldBe Year.of(value = 2026)
            }
        }

        data class InvalidYearTestCase(val value: Int) : WithDataTestName {
            override fun dataTestName(): String = "入力が $value の場合、ValidationError を返す"
        }

        withData(
            InvalidYearTestCase(value = 2028),
            InvalidYearTestCase(value = 0),
        ) { (value) ->
            // execute
            val actual = Year.ofValidate(value = value)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe ValidationError(
                    pointer = "year",
                    invalidValue = value,
                    detail = "value is not supported.",
                )
            }
        }
    }

})
