package org.contourgara

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe
import ulid.ULID

class EnvironmentVariableTest : FunSpec({
    test("環境変数の学習用テスト (jvmArgs の指定が必要)") {
        withEnvironment(mapOf(
            "AAA" to "aaa",
            "BBB" to "bbb",
        )) {
            // assert
            assertSoftly {
                System.getenv("AAA") shouldBe "aaa"
                System.getenv("BBB") shouldBe "bbb"
            }
        }
    }

    test("test") {
        println(ULID.nextULID())
        println(ULID.nextULID())
    }
})
