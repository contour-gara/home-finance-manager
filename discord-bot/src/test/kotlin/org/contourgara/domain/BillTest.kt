package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import ulid.ULID

class BillTest : StringSpec({
    val ulid = ULID.nextULID()

    "インスタンス生成で、請求金額が 0 の場合例外が飛ぶ" {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Bill.of(ulid, 0, "yuki", "gara", "test")
        }.message shouldBe "請求金額は 1 円以上でないとならない: 0"
    }

    "インスタンス生成で、メモがスペースのみの場合例外が飛ぶ" {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Bill.of(ulid, 1, "yuki", "gara", "　 ")
        }.message shouldBe "メモは空文字ではならない"
    }

    "インスタンス生成で、メモが 0 文字の場合例外が飛ぶ" {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Bill.of(ulid, 1, "yuki", "gara", "")
        }.message shouldBe "メモは空文字ではならない"
    }

    "インスタンス生成で、請求者と請求先が同じ場合例外が飛ぶ" {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Bill.of(ulid, 1, "gara", "gara", "")
        }.message shouldBe "請求者と請求先は同じではならない"
    }
})
