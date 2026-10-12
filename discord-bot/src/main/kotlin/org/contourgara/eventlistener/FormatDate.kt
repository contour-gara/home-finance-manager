package org.contourgara.eventlistener

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

fun formatDate(year: Int?, month: Int?, day: Int): String =
    listOfNotNull(
        year?.let { "$it 年" },
        month?.let { "$it 月" },
        "$day 日",
    )
        .joinToString(separator = " ")

fun LocalDate.formatDate(): String = formatDate(year = year, month = month.number, day = day)
