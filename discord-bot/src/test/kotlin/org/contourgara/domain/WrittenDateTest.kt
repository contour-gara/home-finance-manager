package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class WrittenDateTest : FunSpec({
    context("組み合わせから生成できる") {
        data class TestCase(val year: Int?, val month: Int?, val day: Int?, val expected: WrittenDate) : WithDataTestName {
            override fun dataTestName(): String = "year=$year, month=$month, day=$day は $expected になる"
        }

        withData(
            TestCase(null, null, null, WrittenDate.Blank),
            TestCase(null, null, 11, WrittenDate.DayOnly(11)),
            TestCase(null, 8, 3, WrittenDate.MonthDay(8, 3)),
            TestCase(2026, 8, 3, WrittenDate.Full(2026, 8, 3)),
        ) { (year, month, day, expected) ->
            // execute
            val actual = WrittenDate.of(year = year, month = month, day = day)

            // assert
            actual shouldBe expected
        }
    }

    context("不正な組み合わせは例外が飛ぶ") {
        data class TestCase(val year: Int?, val month: Int?, val day: Int?, val description: String) : WithDataTestName {
            override fun dataTestName(): String = "$description (year=$year, month=$month, day=$day) の場合例外が飛ぶ"
        }

        withData(
            TestCase(2026, null, null, "年のみ"),
            TestCase(null, 8, null, "月のみ"),
            TestCase(2026, null, 3, "年 + 日"),
            TestCase(2026, 8, null, "年月"),
        ) { (year, month, day, _) ->
            // execute & assert
            shouldThrowExactly<IllegalArgumentException> {
                WrittenDate.of(year = year, month = month, day = day)
            }.message shouldBe "日付は日のみ、月日、年月日のいずれかでないとならない: year=$year, month=$month, day=$day"
        }
    }

    context("提出日で補完できる") {
        data class TestCase(val writtenDate: WrittenDate, val submittedOn: LocalDate, val expected: LocalDate) : WithDataTestName {
            override fun dataTestName(): String = "$writtenDate を提出日 $submittedOn で補完すると $expected になる"
        }

        withData(
            TestCase(WrittenDate.Blank, LocalDate(2026, 4, 18), LocalDate(2026, 4, 18)),
            TestCase(WrittenDate.DayOnly(11), LocalDate(2026, 4, 18), LocalDate(2026, 4, 11)),
            TestCase(WrittenDate.DayOnly(30), LocalDate(2026, 4, 18), LocalDate(2026, 4, 30)),
            TestCase(WrittenDate.DayOnly(29), LocalDate(2028, 2, 10), LocalDate(2028, 2, 29)),
            TestCase(WrittenDate.MonthDay(8, 3), LocalDate(2026, 4, 18), LocalDate(2026, 8, 3)),
            TestCase(WrittenDate.MonthDay(2, 29), LocalDate(2028, 4, 18), LocalDate(2028, 2, 29)),
            TestCase(WrittenDate.MonthDay(12, 31), LocalDate(2026, 4, 18), LocalDate(2026, 12, 31)),
            TestCase(WrittenDate.Full(2026, 8, 3), LocalDate(2026, 4, 18), LocalDate(2026, 8, 3)),
            TestCase(WrittenDate.Full(2024, 2, 29), LocalDate(2026, 4, 18), LocalDate(2024, 2, 29)),
        ) { (writtenDate, submittedOn, expected) ->
            // execute
            val actual = writtenDate.complete(submittedOn = submittedOn)

            // assert
            actual shouldBe expected
        }
    }

    context("存在しない日付は例外が飛ぶ") {
        data class TestCase(val writtenDate: WrittenDate, val submittedOn: LocalDate, val expected: String) : WithDataTestName {
            override fun dataTestName(): String = "$writtenDate を提出日 $submittedOn で補完すると「$expected」の例外が飛ぶ"
        }

        withData(
            TestCase(WrittenDate.DayOnly(31), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 4 月 31 日"),
            TestCase(WrittenDate.DayOnly(29), LocalDate(2026, 2, 10), "存在しない日付: 2026 年 2 月 29 日"),
            TestCase(WrittenDate.MonthDay(11, 31), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 11 月 31 日"),
            TestCase(WrittenDate.MonthDay(2, 29), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 2 月 29 日"),
            TestCase(WrittenDate.Full(2026, 2, 29), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 2 月 29 日"),
            TestCase(WrittenDate.Full(2026, 11, 31), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 11 月 31 日"),
            TestCase(WrittenDate.DayOnly(0), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 4 月 0 日"),
            TestCase(WrittenDate.DayOnly(32), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 4 月 32 日"),
            TestCase(WrittenDate.MonthDay(13, 1), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 13 月 1 日"),
            TestCase(WrittenDate.Full(2026, 0, 1), LocalDate(2026, 4, 18), "存在しない日付: 2026 年 0 月 1 日"),
        ) { (writtenDate, submittedOn, expected) ->
            // execute & assert
            shouldThrowExactly<IllegalArgumentException> {
                writtenDate.complete(submittedOn = submittedOn)
            }.message shouldBe expected
        }
    }
})
