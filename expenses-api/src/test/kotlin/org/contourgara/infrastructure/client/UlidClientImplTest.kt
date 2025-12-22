package org.contourgara.infrastructure.client

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldHaveLength

class UlidClientImplTest : FunSpec({
    test("仮実装") {
        // setup
        val sut = UlidClientImpl()

        // execute
        val actual = sut.nextUlid()

        // assert
        actual.toString() shouldHaveLength 26
    }
})
