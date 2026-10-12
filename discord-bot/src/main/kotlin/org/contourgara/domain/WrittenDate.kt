package org.contourgara.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

sealed interface WrittenDate {
    fun complete(submittedOn: LocalDate): LocalDate

    data object Blank : WrittenDate {
        override fun complete(submittedOn: LocalDate): LocalDate = submittedOn
    }

    data class DayOnly(val day: Int) : WrittenDate {
        override fun complete(submittedOn: LocalDate): LocalDate =
            localDateOf(year = submittedOn.year, month = submittedOn.month.number, day = day)
    }

    data class MonthDay(val month: Int, val day: Int) : WrittenDate {
        override fun complete(submittedOn: LocalDate): LocalDate =
            localDateOf(year = submittedOn.year, month = month, day = day)
    }

    data class Full(val year: Int, val month: Int, val day: Int) : WrittenDate {
        override fun complete(submittedOn: LocalDate): LocalDate =
            localDateOf(year = year, month = month, day = day)
    }

    companion object {
        fun of(year: Int?, month: Int?, day: Int?): WrittenDate =
            when {
                year == null && month == null && day == null -> Blank
                year == null && month == null && day != null -> DayOnly(day = day)
                year == null && month != null && day != null -> MonthDay(month = month, day = day)
                year != null && month != null && day != null -> Full(year = year, month = month, day = day)
                else -> throw IllegalArgumentException("日付は日のみ、月日、年月日のいずれかでないとならない: year=$year, month=$month, day=$day")
            }
    }
}

private fun localDateOf(year: Int, month: Int, day: Int): LocalDate =
    try {
        LocalDate(year = year, month = month, day = day)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("存在しない日付: $year 年 $month 月 $day 日", e)
    }
