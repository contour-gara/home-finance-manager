package org.contourgara.eventlistener

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UserTest : StringSpec({
    "ID が 703805458116509818 の場合 GARA が返る" {
        // execute
        val actual = User.of(703805458116509818)

        // assert
        val expected = User.GARA
        actual shouldBe expected
    }

    "ID が 889339009061507143 の場合 YUKI が返る" {
        // execute
        val actual = User.of(889339009061507143)

        // assert
        val expected = User.YUKI
        actual shouldBe expected
    }

    "無効な ID の場合 UNDEFINED が返る" {
        // execute
        val actual = User.of(1)

        // assert
        val expected = User.UNDEFINED
        actual shouldBe expected
    }

    "名前が gara の場合 GARA が返る" {
        // execute
        val actual = User.of("gara")

        // assert
        val expected = User.GARA
        actual shouldBe expected
    }

    "名前が yuki の場合 YUKI が返る" {
        // execute
        val actual = User.of("yuki")

        // assert
        val expected = User.YUKI
        actual shouldBe expected
    }

    "名前が gara か yuki 以外の場合 UNDEFINED が返る" {
        // execute
        val actual = User.of("a")

        // assert
        val expected = User.UNDEFINED
        actual shouldBe expected
    }
})
