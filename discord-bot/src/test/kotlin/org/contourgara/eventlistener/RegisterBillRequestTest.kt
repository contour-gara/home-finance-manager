package org.contourgara.eventlistener

import arrow.core.nonEmptyListOf
import dev.kord.common.Color
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.EmbedFieldData
import dev.kord.rest.builder.message.EmbedBuilder
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import org.contourgara.application.RegisterBillParam
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError

class RegisterBillRequestTest : StringSpec({
    val GARA_ID = 703805458116509818

    "請求金額からインスタンスを生成できる" {
        // execute
        val actual = RegisterBillRequest.of(1)

        // assert
        assertSoftly {
            actual.shouldBeRight()
            actual.getOrNull()?.amount shouldBe 1
            actual.getOrNull()?.claimant shouldBe User.UNDEFINED
            actual.getOrNull()?.memo.shouldBeEmpty()
        }
    }

    "請求金額が 0 の場合インスタンスを生成できない" {
        // execute
        val actual = RegisterBillRequest.of(0)

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 1
            actual.value.first() shouldBe RegisterBillValidationError.AmountError.of(0)
        }
    }

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
            actual.shouldBeRight()
            actual.getOrNull()?.amount shouldBe 1
            actual.getOrNull()?.claimant shouldBe User.GARA
            actual.getOrNull()?.memo shouldBe "test"
        }
    }

    "EmbedData と userId とメモからインスタンスを生成で、請求金額が 0 または無効な userId またはメモが空白のみの場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "0 円")
            ))
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, 0, " 　")

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 3
            actual.value shouldBe listOf(
                RegisterBillValidationError.AmountError.of(0),
                RegisterBillValidationError.ClaimantError.of(User.UNDEFINED),
                RegisterBillValidationError.MemoError.of(" 　")
            )
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
            actual.shouldBeLeft()
            actual.value shouldHaveSize 1
            actual.value shouldBe nonEmptyListOf(
                RegisterBillValidationError.MemoError.of("")
            )
        }
    }

    "EmbedData と userId とメモからインスタンスを生成で、EmbedData のタイトルとカラーが不正で fields が空の場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("test"),
            color = OptionalInt.Value(Color(0, 0, 0).rgb),
            fields = Optional.Value(emptyList())
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, GARA_ID, "test")

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 3
            actual.value shouldBe nonEmptyListOf(
                RegisterBillValidationError.EmbedDataTitleError.of("test"),
                RegisterBillValidationError.EmbedDataColorError.of(Color(0, 0, 0), "黄色"),
                RegisterBillValidationError.EmbedDataFieldNamesError.of(emptyList(), listOf("請求金額だっピ"))
            )
        }
    }

    "EmbedData と userId とメモからインスタンスを生成で、EmbedData の請求金額フォーマットが不正な場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1円")
            ))
        )

        // execute
        val actual = RegisterBillRequest.of(embedData, GARA_ID, "test")

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 1
            actual.value shouldBe nonEmptyListOf(
                RegisterBillValidationError.EmbedDataFieldAmountFormatError.of("1円")
            )
        }
    }

    "Embed を生成できる" {
        // setup
        val sut = RegisterBillRequest.of(1).getOrNull()!!

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

        val sut = RegisterBillRequest.of(embedData, GARA_ID, "test").getOrNull()!!

        // execute
        val actual = sut.toParam()

        // assert
        val expected = RegisterBillParam(1, "gara", "test")
        actual shouldBe expected
    }
})
