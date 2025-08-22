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
            Bill(ulid, 0, User.of("gara"), "test")
        }.message shouldBe "請求金額は [0..Int.MAX_VALUE] でないとならない: 0"
    }

    "インスタンス生成で、メモがスペースのみの場合例外が飛ぶ" {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Bill(ulid, 1, User.of("gara"), "　 ")
        }.message shouldBe "メモは空文字ではならない"
    }

    "インスタンス生成で、メモが 0 文字の場合例外が飛ぶ" {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            Bill(ulid, 1, User.of("gara"), "")
        }.message shouldBe "メモは空文字ではならない"
    }
})
