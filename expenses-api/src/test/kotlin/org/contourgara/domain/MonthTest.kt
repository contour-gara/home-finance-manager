package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
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
})
