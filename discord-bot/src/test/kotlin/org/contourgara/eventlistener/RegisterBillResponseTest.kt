package org.contourgara.eventlistener

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
import org.contourgara.application.RegisterBillDto
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError

class RegisterBillResponseTest : StringSpec({
    "DTO からインスタンスを生成できる" {
        // setup
        val registerBillDto = RegisterBillDto("ID", 1, "gara", "test")

        // execute
        val actual = RegisterBillResponse.from(registerBillDto)

        // assert
        assertSoftly {
            actual.shouldBeRight()
            actual.getOrNull()?.id shouldBe "ID"
            actual.getOrNull()?.amount shouldBe 1
            actual.getOrNull()?.claimant shouldBe User.GARA
            actual.getOrNull()?.memo shouldBe "test"
        }
    }

    "DTO からインスタンスで、各項目が不正な場合インスタンスを生成できない" {
        // setup
        val registerBillDto = RegisterBillDto("ID", 0, "test", "")

        // execute
        val actual = RegisterBillResponse.from(registerBillDto)

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 3
            actual.value shouldBe listOf(
                RegisterBillValidationError.AmountError.of(0),
                RegisterBillValidationError.ClaimantError.of(User.UNDEFINED),
                RegisterBillValidationError.MemoError.of(""),
            )
        }
    }

    "EmbedData からインスタンスを生成できる" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(0, 255, 0).rgb),
            fields = Optional.Value(
                listOf(
                    EmbedFieldData(name = "申請 ID だっピ", inline = OptionalBoolean.Value(true), value = "ID"),
                    EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1 円"),
                    EmbedFieldData(name = "請求者だっピ", inline = OptionalBoolean.Value(true), value = "gara"),
                    EmbedFieldData(name = "メモだっピ", inline = OptionalBoolean.Value(true), value = "test")
                )
            )
        )

        // execute
        val actual = RegisterBillResponse.from(embedData)

        // assert
        assertSoftly {
            actual.shouldBeRight()
            actual.getOrNull()?.id shouldBe "ID"
            actual.getOrNull()?.amount shouldBe 1
            actual.getOrNull()?.claimant shouldBe User.GARA
            actual.getOrNull()?.memo shouldBe "test"
        }
    }

    "EmbedData からインスタンスを生成で、EmbedData が不正の場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(emptyList())
        )

        // execute
        val actual = RegisterBillResponse.from(embedData)

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 3
            actual.value shouldBe listOf(
                RegisterBillValidationError.EmbedDataTitleError.of("入力情報"),
                RegisterBillValidationError.EmbedDataColorError.of(Color(255, 255, 50), "緑色"),
                RegisterBillValidationError.EmbedDataFieldNamesError.of(emptyList(), listOf("申請 ID だっピ", "請求金額だっピ", "請求者だっピ", "メモだっピ"))
            )
        }
    }

    "EmbedData からインスタンスを生成で、EmbedData の請求金額のフォーマット不正の場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(0, 255, 0).rgb),
            fields = Optional.Value(
                listOf(
                    EmbedFieldData(name = "申請 ID だっピ", inline = OptionalBoolean.Value(true), value = "ID"),
                    EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1円"),
                    EmbedFieldData(name = "請求者だっピ", inline = OptionalBoolean.Value(true), value = "gara"),
                    EmbedFieldData(name = "メモだっピ", inline = OptionalBoolean.Value(true), value = "test")
                )
            )
        )

        // execute
        val actual = RegisterBillResponse.from(embedData)

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 1
            actual.value shouldBe listOf(
                RegisterBillValidationError.EmbedDataFieldAmountFormatError.of("1円")
            )
        }
    }

    "EmbedData からインスタンスを生成で、各項目が不正な値の場合インスタンスを生成できない" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(0, 255, 0).rgb),
            fields = Optional.Value(
                listOf(
                    EmbedFieldData(name = "申請 ID だっピ", inline = OptionalBoolean.Value(true), value = "ID"),
                    EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "0 円"),
                    EmbedFieldData(name = "請求者だっピ", inline = OptionalBoolean.Value(true), value = "test"),
                    EmbedFieldData(name = "メモだっピ", inline = OptionalBoolean.Value(true), value = "")
                )
            )
        )

        // execute
        val actual = RegisterBillResponse.from(embedData)

        // assert
        assertSoftly {
            actual.shouldBeLeft()
            actual.value shouldHaveSize 3
            actual.value shouldBe listOf(
                RegisterBillValidationError.AmountError.of(0),
                RegisterBillValidationError.ClaimantError.of(User.UNDEFINED),
                RegisterBillValidationError.MemoError.of(""),
            )
        }
    }

    "Embed を生成できる" {
        // setup
        val sut = RegisterBillResponse.from(RegisterBillDto("ID", 1, "gara", "test")).getOrNull()!!

        // execute
        val actual = EmbedBuilder().apply(sut.toEmbedBuilder()).toRequest()

        // assert
        val expected = EmbedBuilder().apply {
            title = "入力情報だっピ"
            color = Color(0, 255, 0)
            field(name = "申請 ID だっピ", inline = true, value = { "ID" })
            field(name = "請求金額だっピ", inline = true, value = { "1 円" })
            field(name = "請求者だっピ", inline = true, value = { "gara" })
            field(name = "メモだっピ", inline = true, value = { "test" })
        }.toRequest()
        actual shouldBe expected
    }
})
