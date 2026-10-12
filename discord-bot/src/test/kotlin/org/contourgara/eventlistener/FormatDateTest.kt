package org.contourgara.eventlistener

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class FormatDateTest : FunSpec({
    context("年月日を日付にフォーマットできる") {
        data class TestCase(val year: Int?, val month: Int?, val day: Int, val expected: String) : WithDataTestName {
            override fun dataTestName(): String = "year: $year, month: $month, day: $day は $expected にフォーマットできる"
        }

        withData(
            TestCase(year = null, month = null, day = 11, expected = "11 日"),
            TestCase(year = null, month = 8, day = 3, expected = "8 月 3 日"),
            TestCase(year = 2026, month = 8, day = 3, expected = "2026 年 8 月 3 日"),
            TestCase(year = 2026, month = 12, day = 25, expected = "2026 年 12 月 25 日"),
        ) { (year, month, day, expected) ->
            // execute
            val actual = formatDate(year = year, month = month, day = day)

            // assert
            actual shouldBe expected
        }
    }

    test("LocalDate を日付にフォーマットできる") {
        // setup
        val sut = LocalDate(year = 2026, month = 4, day = 11)

        // execute
        val actual = sut.formatDate()

        // assert
        actual shouldBe "2026 年 4 月 11 日"
    }
})
