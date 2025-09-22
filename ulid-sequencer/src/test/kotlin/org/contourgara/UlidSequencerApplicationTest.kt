package org.contourgara

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.testing.testApplication
import ulid.ULID

class UlidSequencerApplicationTest : FunSpec({
    test("ルートエンドポイントにアクセスすると、'Hello World!' が取得できる") {
        testApplication {
            // setup
            application {
                module()
            }

            // execute
            val actual = client.get("/")

            // assert
            actual shouldHaveStatus 200
            actual.bodyAsText() shouldBe "Hello World!"
        }
    }

    test("next-ulid エンドポイントにアクセスすると、ULID 形式の文字列が取得できる") {
        testApplication {
            // setup
            application {
                module()
            }

            // execute
            val actual = client.get("/next-ulid")

            // assert
            actual shouldHaveStatus 200
            ULID.parseULID(actual.bodyAsText()).shouldNotBeNull()
        }
    }
})
