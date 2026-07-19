package org.contourgara.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class OldExpenseTest : FunSpec({
    context("メモから日付に変換できる") {
        withTests(
            Pair(OldExpense(id = "test", year = 2026, month = 1, memo = "20260103 test"), LocalDate(year = 2026, month = 1, day = 3)),
            Pair(OldExpense(id = "test", year = 2027, month = 2, memo = "20270204 2/4 test"), LocalDate(year = 2027, month = 2, day = 4)),
            Pair(OldExpense(id = "test", year = 2026, month = 1, memo = "1/4 test"), LocalDate(year = 2026, month = 1, day = 4)),
            Pair(OldExpense(id = "test", year = 2027, month = 2, memo = "2/7 1/4 test"), LocalDate(year = 2027, month = 2, day = 7)),
            Pair(OldExpense(id = "test", year = 2026, month = 1, memo = "1/5,6,7,9のお昼ご飯代(社食)"), LocalDate(year = 2026, month = 1, day = 5)),
            Pair(OldExpense(id = "test", year = 2026, month = 1, memo = "test"), LocalDate(year = 2026, month = 1, day = 1)),
            Pair(OldExpense(id = "test", year = 2027, month = 2, memo = "test"), LocalDate(year = 2027, month = 2, day = 1)),
        ) { (sut, expected) ->
            // execute
            val actual = sut.dateFromMemo()

            // asset
            actual shouldBe expected
        }
    }
})
