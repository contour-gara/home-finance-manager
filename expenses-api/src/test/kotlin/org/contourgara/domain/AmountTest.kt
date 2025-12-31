package org.contourgara.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AmountTest : FunSpec({
    test("Amount の加算ができる") {
        // setup
        val amount1 = Amount(value = 1000)
        val amount2 = Amount(value = 500)

        // execute
        val actual = amount1 + amount2

        // assert
        val expected = Amount(value = 1500)
        actual shouldBe expected
    }
})
