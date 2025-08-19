package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.EmbedFieldData
import dev.kord.rest.builder.message.EmbedBuilder
import io.konform.validation.Valid
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import org.contourgara.application.RegisterBillParam

class RegisterBillRequestTest : StringSpec({
    "請求金額からインスタンスを生成できる" {
        // execute
        val actual = RegisterBillRequest.of(1)

        // assert
        assertSoftly {
            actual.isValid shouldBe true
            actual.map {
                it.amount shouldBe 1
                it.claimant shouldBe User.UNDEFINED
                it.memo.shouldBeEmpty()
            }
        }
    }

    "請求金額が 0 の場合インスタンスを生成できない" {
        // execute
        val actual = RegisterBillRequest.of(0)

        // assert
        assertSoftly {
            actual.isValid shouldBe false
            actual.errors shouldHaveSize 1
            actual.errors.first().message shouldBe "請求金額は 1 円未満ではならない"
        }
    }

    val GARA_ID = 703805458116509818

    "EmbedData と userId とメモからインスタンスを生成できる" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1 円")
            ))
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, GARA_ID, "test")

        // assert
        assertSoftly {
            actual.isValid shouldBe true
            actual.map {
                it.amount shouldBe 1
                it.claimant shouldBe User.GARA
                it.memo shouldBe "test"
            }
        }
    }

    "EmbedData と userId とメモからインスタンスを生成で、請求金額が 0 の場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "0 円")
            ))
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, GARA_ID, "test")

        // assert
        assertSoftly {
            actual.isValid shouldBe false
            actual.errors shouldHaveSize 1
            actual.errors.first().message shouldBe "請求金額は 1 円未満ではならない"
        }
    }

    "EmbedData と userId とメモからインスタンスを生成で、無効な userId の場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1 円")
            ))
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, 1, "test")

        // assert
        assertSoftly {
            actual.isValid shouldBe false
            actual.errors shouldHaveSize 1
            actual.errors.first().message shouldBe "請求者は gara か yuki でないとならない"
        }
    }

    "EmbedData と userId とメモからインスタンスを生成で、メモが空白のみの場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1 円")
            ))
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, GARA_ID, "　 ")

        // assert
        assertSoftly {
            actual.isValid shouldBe false
            actual.errors shouldHaveSize 1
            actual.errors.first().message shouldBe "メモは空白のみではならない"
        }
    }

    "EmbedData と userId とメモからインスタンスを生成で、メモが 0 文字の場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1 円")
            ))
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, GARA_ID, "")

        // assert
        assertSoftly {
            actual.isValid shouldBe false
            actual.errors shouldHaveSize 2
            actual.errors.first().message shouldBe "メモは空白のみではならない"
            actual.errors[1].message shouldBe "メモは 1 文字未満ではならない"
        }
    }

    "Embed を生成できる" {
        // setup
        val sut = RegisterBillRequest.of(1).let { if (it is Valid) it.value else null }!!

        // execute
        val actual = EmbedBuilder().apply(sut.toEmbedBuilder()).toRequest()

        // assert
        val expected = EmbedBuilder().apply {
            title = "入力情報だっピ"
            color = Color(255, 255, 50)
            field(name = "請求金額だっピ", inline = true, value = { "1 円" })
        }.toRequest()
        actual shouldBe expected
    }

    "Param を生成できる" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1 円")
            ))
        )

        val sut = RegisterBillRequest.of(embedData, GARA_ID, "test").let { if (it is Valid) it.value else null }!!

        // execute
        val actual = sut.toParam()

        // assert
        val expected = RegisterBillParam(1, "gara", "test")
        actual shouldBe expected
    }
})
