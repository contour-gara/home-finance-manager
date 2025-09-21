package org.contourgara.eventlistener

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe

class ParseAmountTest : FunSpec({
    context("請求金額を Int にパースできる") {
        data class TestCase(val input: String, val expected: String) : WithDataTestName {
            override fun dataTestName(): String = "$input は $expected にパースできる"
        }

        withData(
            TestCase("1,000 円", "1000"),
            TestCase("1,000,000 円", "1000000"),
            TestCase("100 円", "100"),
            TestCase("0 円", "0"),
        ) { (input, expected) ->
            // execute
            val actual = input.parseAmount()

            // assert
            actual shouldBe expected
        }
    }
})
