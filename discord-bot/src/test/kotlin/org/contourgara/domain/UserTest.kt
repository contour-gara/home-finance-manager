package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UserTest : StringSpec({
    "インスタンス生成で、存在しないユーザー名の場合例外が飛ぶ" {
        // execute & assert
        shouldThrowExactly<RuntimeException> {
            User.of("test")
        }.message shouldBe "ユーザーが存在しない: test"
    }

    "小文字の名前を取得できる" {
        // setup
        val sut = User.of("gara")

        // execute
        val actual = sut.lowercaseName()

        // assert
        val expected = "gara"
        actual shouldBe expected
    }
})
