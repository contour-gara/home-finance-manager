package org.contourgara.generator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan

class UlidGeneratorTest : FunSpec({
    context("Stateful に ULID を生成するメソッドのテスト") {
        test("2 度 ULID を生成したら後者が前者よりも大きい") {
            // execute
            val ulid1 = generateNextUlidByStateful()
            val ulid2 = generateNextUlidByStateful()

            // assert
            ulid2 shouldBeGreaterThan ulid1
        }
    }
})