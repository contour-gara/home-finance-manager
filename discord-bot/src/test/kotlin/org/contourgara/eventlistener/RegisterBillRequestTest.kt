package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.contourgara.application.RegisterBillParam

class RegisterBillRequestTest : StringSpec({
    val GARA_ID = 703805458116509818
    val YUKI_ID = 889339009061507143

    "Embed を生成できる" {
        // setup
        val sut = RegisterBillRequest.of("1", YUKI_ID, GARA_ID, "test").getOrNull()!!

        // execute
        val actual = EmbedBuilder().apply(sut.toEmbedBuilder()).toRequest()

        // assert
        val expected = EmbedBuilder().apply {
            title = "入力情報だっピ"
            color = Color(255, 255, 50)
            field(name = "請求金額", inline = true, value = { "1 円" })
            field(name = "請求者", inline = true, value = { "yuki" })
            field(name = "請求先", inline = true, value = { "gara" })
            field(name = "メモ", inline = true, value = { "test" })
        }.toRequest()
        actual shouldBe expected
    }

    "Param を生成できる" {
        // setup
        val sut = RegisterBillRequest.of("1", YUKI_ID, GARA_ID, "test").getOrNull()!!

        // execute
        val actual = sut.toParam()

        // assert
        val expected = RegisterBillParam(1, "yuki", "gara", "test")
        actual shouldBe expected
    }
})
