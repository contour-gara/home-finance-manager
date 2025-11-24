package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.EmbedBuilder
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.contourgara.application.RegisterBillDto

class RegisterBillResponseTest : StringSpec({
    "インスタンス生成で、DTO の各パラメータが適切な場合、Right が返る" {
        // setup
        val registerBillDto = RegisterBillDto("ID", 1, "yuki", "gara", "test")

        // execute
        val actual = RegisterBillResponse.from(registerBillDto)

        // assert
        actual.shouldBeRight()
    }

    "Embed を生成できる" {
        // setup
        val sut = RegisterBillResponse.from(RegisterBillDto("ID", 1, "yuki", "gara", "test")).getOrNull()!!

        // execute
        val actual = EmbedBuilder().apply(sut.toEmbedBuilder()).toRequest()

        // assert
        val expected = EmbedBuilder().apply {
            title = "入力情報だっピ"
            color = Color(0, 255, 0)
            field(name = "請求 ID", inline = true, value = { "ID" })
            field(name = "請求金額", inline = true, value = { "1 円" })
            field(name = "請求者", inline = true, value = { "yuki" })
            field(name = "請求先", inline = true, value = { "gara" })
            field(name = "メモ", inline = true, value = { "test" })
        }.toRequest()

        actual shouldBe expected
    }

    "請求先の Snowflake ID を取得できる" {
        // setup
        val GARA_ID = 703805458116509818
        val sut = RegisterBillResponse.from(RegisterBillDto("ID", 1, "yuki", "gara", "test")).getOrNull()!!

        // execute
        val actual = sut.getBorrowerId()

        // assert
        val expected = Snowflake(GARA_ID)
        actual shouldBe expected
    }
})
