package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.EmbedFieldData
import dev.kord.rest.builder.message.EmbedBuilder
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.contourgara.application.RegisterBillDto

class RegisterBillResponseTest : StringSpec({
    "DTO からインスタンスを生成できる" {
        // setup
        val registerBillDto = RegisterBillDto("ID", 1, "gara", "test")

        // execute
        val actual = RegisterBillResponse.fromDto(registerBillDto)

        // assert
        assertSoftly {
            actual.id shouldBe "ID"
            actual.amount shouldBe 1
            actual.claimant shouldBe User.GARA
            actual.memo shouldBe "test"
        }
    }

    "EmbedData からインスタンスを生成できる" {
        // setup
        val embedData = EmbedData(
            title = Optional.Value("入力情報だっピ"),
            color = OptionalInt.Value(Color(255, 255, 50).rgb),
            fields = Optional.Value(listOf(
                EmbedFieldData(name = "申請 ID だっピ", inline = OptionalBoolean.Value(true), value = "ID"),
                EmbedFieldData(name = "請求金額だっピ", inline = OptionalBoolean.Value(true), value = "1 円"),
                EmbedFieldData(name = "請求者だっピ", inline = OptionalBoolean.Value(true), value = "gara"),
                EmbedFieldData(name = "メモだっピ", inline = OptionalBoolean.Value(true), value = "test")
            ))
        )

        // execute
        val actual = RegisterBillResponse.fromEmbedData(embedData)

        // assert
        assertSoftly {
            actual.id shouldBe "ID"
            actual.amount shouldBe 1
            actual.claimant shouldBe User.GARA
            actual.memo shouldBe "test"
        }
    }

    "Embed を生成できる" {
        // setup
        val sut = RegisterBillResponse.fromDto(RegisterBillDto("ID", 1, "gara", "test"))

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
