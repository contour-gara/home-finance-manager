package org.contourgara.eventlistener

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe

class FormatAmountTest : FunSpec({
    context("Int を請求金額にフォーマットできる") {
        data class TestCase(val input: Int, val expected: String) : WithDataTestName {
            override fun dataTestName(): String = "$input は $expected にフォーマットできる"
        }

        withData(
            TestCase(1_000, "1,000 円"),
            TestCase(1_000_000, "1,000,000 円"),
            TestCase(100, "100 円"),
            TestCase(0, "0 円"),
        ) { (input, expected) ->
            // execute
            val actual = input.formatAmount()

            // assert
            actual shouldBe expected
        }
    }

})
