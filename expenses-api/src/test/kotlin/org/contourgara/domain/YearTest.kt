package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class YearTest : FunSpec({
    test("Int から Year を取得できる") {
        // execute
        val actual = Year.of(intYear = 2026)

        // assert
        val expected = Year._2026
        actual shouldBe expected
    }

    test("Int から Year 取得で不適切な値の場合は例外が投げられる") {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Year.of(intYear = 2025)
        }.message shouldBe "Not found: 2025"
    }
})
