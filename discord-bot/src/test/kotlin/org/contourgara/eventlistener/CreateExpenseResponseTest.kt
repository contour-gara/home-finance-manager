package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import org.contourgara.application.CreateExpenseDto

class CreateExpenseResponseTest : StringSpec({
    "Embed を生成できる" {
        // setup
        val sut = CreateExpenseResponse.fromDto(
            createExpenseDto = CreateExpenseDto(
                amount = 1000,
                category = "FOOD",
                payer = "gara",
                localDate = LocalDate(year = 2026, month = 4, day = 11),
                memo = "test",
            ),
        )

        // execute
        val actual = EmbedBuilder().apply(sut.toEmbedBuilder()).toRequest()

        // assert
        val expected = EmbedBuilder().apply {
            title = "入力情報だっピ"
            color = Color(red = 0, green = 255, blue = 0)
            field(name = "支出金額", inline = true, value = { "1,000 円" })
            field(name = "支払い者", inline = true, value = { "gara" })
            field(name = "支出カテゴリー", inline = true, value = { "FOOD" })
            field(name = "日付", inline = true, value = { "2026 年 4 月 11 日" })
            field(name = "メモ", inline = true, value = { "test" })
        }.toRequest()

        actual shouldBe expected
    }
})
