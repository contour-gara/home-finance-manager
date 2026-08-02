package org.contourgara.domain

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class ValidatedDateTest : FunSpec({
    test("サポートしている年の場合、right に ValidatedDate が返る") {
        // execute
        val actual = ValidatedDate.of(value = LocalDate(year = 2026, month = 1, day = 1))

        // assert
        assertSoftly {
            actual.shouldBeRight()
            actual.value.value shouldBe LocalDate(year = 2026, month = 1, day = 1)
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
        val actual = ValidatedDate.of(value = LocalDate(year = value, month = 1, day = 1 ))

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 1
            actual.value.first() shouldBe ValidationError(
                pointer = "validatedDate",
                invalidValue = "${"%04d".format(value)}-01-01",
                detail = "value is not supported.",
            )
        }
    }
})
