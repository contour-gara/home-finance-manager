package org.contourgara.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ulid.ULID

class ULIDTest : FunSpec({
    test("学習用テスト: ULID のパース失敗確認 1") {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            ULID.parseULID("test")
        }.message shouldBe "ulid string must be exactly 26 chars long"
    }

    test("学習用テスト: ULID のパース失敗確認 2") {
        // execute & assert
        shouldThrowExactly<IllegalArgumentException> {
            ULID.parseULID("aaaaaaaaaaaaaaaaaaaaaaaaaa")
        }.message shouldBe "ulid string must not exceed '7ZZZZZZZZZZZZZZZZZZZZZZZZZ'!"
    }
})
