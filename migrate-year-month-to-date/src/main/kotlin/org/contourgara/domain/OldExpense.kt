package org.contourgara.domain

import kotlinx.datetime.LocalDate

data class OldExpense(
    val id: String,
    val year: Int,
    val month: Int,
    val memo: String,
) {
    // 採用
    fun haveyyyyMMdd(): Boolean =
        memo.contains(regex = Regex(pattern = """(?<!\d)(\d{4})(\d{2})(\d{2})(?!\d)"""))

    // 最初の日付を採用する
    fun haveSlashes(): Boolean =
        memo.indexOfFirst { it == '/'} != memo.indexOfLast { it == '/'}

    // 採用
    fun haveSlash(): Boolean = memo.contains(char = '/')

    fun dateFromMemo(): LocalDate {
        Regex(pattern = """(?<!\d)(\d{4})(\d{2})(\d{2})(?!\d)""")
            .find(input = memo)
            ?.destructured
            ?.let { (yyyy, mm, dd) ->
                if (year != yyyy.toInt()) throw RuntimeException("Year is different. $this")
                if (month != mm.toInt()) throw RuntimeException("Month is different. $this")
                return LocalDate(year = yyyy.toInt(), month = mm.toInt(), day = dd.toInt())
            }
        Regex(pattern = """(?<!\d)(\d{1,2})/(\d{1,2})(?!\d)""")
            .find(input = memo)
            ?.destructured
            ?.let { (m, d) ->
                if (month != m.toInt()) throw RuntimeException("Month is different. $this")
                return LocalDate(year = year, month = m.toInt(), day = d.toInt())
            }
        return LocalDate(year = year, month = month, day = 1)
    }
}
