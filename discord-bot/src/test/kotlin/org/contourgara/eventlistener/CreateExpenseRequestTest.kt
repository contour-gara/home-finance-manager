package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.EmbedFieldData
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.rest.builder.message.EmbedBuilder
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.contourgara.application.CreateExpenseParam

class CreateExpenseRequestTest : FunSpec({
    data class DateCase(val year: Int?, val month: Int?, val day: Int?, val dateField: String?) : WithDataTestName {
        override fun dataTestName(): String = "year: $year, month: $month, day: $day と日付フィールド $dateField"
    }

    val dateCases = listOf(
        DateCase(year = null, month = null, day = null, dateField = null),
        DateCase(year = null, month = null, day = 11, dateField = "11 日"),
        DateCase(year = null, month = 8, day = 3, dateField = "8 月 3 日"),
        DateCase(year = 2026, month = 8, day = 3, dateField = "2026 年 8 月 3 日"),
    )

    context("Embed の日付フィールドを生成できる") {
        withData(dateCases) { (year, month, day, dateField) ->
            // setup
            val sut = CreateExpenseRequest(amount = 1000, payer = null, category = null, year = year, month = month, day = day, memo = null)

            // execute
            val actual = EmbedBuilder().apply(sut.toEmbedBuilder()).toRequest()

            // assert
            val expected = EmbedBuilder().apply {
                title = "入力情報だっピ"
                color = Color(red = 255, green = 255, blue = 50)
                field(name = "支出金額", inline = true, value = { "1,000 円" })
                dateField?.let { field(name = "日付", inline = true, value = { it }) }
            }.toRequest()
            actual shouldBe expected
        }
    }

    context("Embed から日付を読み戻せる") {
        withData(dateCases) { (year, month, day, dateField) ->
            // setup
            val embedData = EmbedData(
                fields = Optional.Value(
                    listOfNotNull(
                        EmbedFieldData(name = "支出金額", value = "1,000 円", inline = OptionalBoolean.Value(true)),
                        dateField?.let { EmbedFieldData(name = "日付", value = it, inline = OptionalBoolean.Value(true)) },
                    ),
                ),
            )

            // execute
            val actual = CreateExpenseRequest.fromEmbedData(embedData = embedData)

            // assert
            val expected = CreateExpenseRequest(amount = 1000, payer = null, category = null, year = year, month = month, day = day, memo = null)
            actual shouldBe expected
        }
    }

    test("日付フィールドの形式が不正な場合例外が飛ぶ") {
        // setup
        val embedData = EmbedData(
            fields = Optional.Value(
                listOf(
                    EmbedFieldData(name = "支出金額", value = "1,000 円", inline = OptionalBoolean.Value(true)),
                    EmbedFieldData(name = "日付", value = "2026-08-03", inline = OptionalBoolean.Value(true)),
                ),
            ),
        )

        // execute & assert
        shouldThrowExactly<IllegalArgumentException> { CreateExpenseRequest.fromEmbedData(embedData = embedData) }
            .message shouldBe "日付の形式が不正: 2026-08-03"
    }

    test("Param を生成できる") {
        // setup
        val sut = CreateExpenseRequest(amount = 1000, payer = "GARA", category = "FOOD", year = 2026, month = 8, day = 3, memo = "test")

        // execute
        val actual = sut.toParam(messageId = Snowflake(value = 0))

        // assert
        val expected = CreateExpenseParam(messageId = Snowflake(value = 0), amount = 1000, category = "FOOD", payer = "GARA", year = 2026, month = 8, day = 3, memo = "test")
        actual shouldBe expected
    }

    context("コマンド引数から生成できる") {
        data class TestCase(val description: String, val arguments: Map<String, Long>, val year: Int?, val month: Int?, val day: Int?) : WithDataTestName {
            override fun dataTestName(): String = "$description の場合、year: $year, month: $month, day: $day で生成できる"
        }

        withData(
            TestCase(description = "日付なし", arguments = mapOf("amount" to 1000L), year = null, month = null, day = null),
            TestCase(description = "日のみ", arguments = mapOf("amount" to 1000L, "day" to 11L), year = null, month = null, day = 11),
            TestCase(description = "月日", arguments = mapOf("amount" to 1000L, "month" to 8L, "day" to 3L), year = null, month = 8, day = 3),
            TestCase(description = "年月日", arguments = mapOf("amount" to 1000L, "year" to 2026L, "month" to 8L, "day" to 3L), year = 2026, month = 8, day = 3),
        ) { (_, arguments, year, month, day) ->
            // setup
            val interactionCommand = mockk<InteractionCommand> {
                every { integers } returns arguments
            }

            // execute
            val actual = CreateExpenseRequest.from(interactionCommand = interactionCommand)

            // assert
            val expected = CreateExpenseRequest(amount = 1000, payer = null, category = null, year = year, month = month, day = day, memo = null)
            actual shouldBe expected
        }
    }

    context("コマンド引数の不正な組み合わせは例外が飛ぶ") {
        data class TestCase(val description: String, val arguments: Map<String, Long>) : WithDataTestName {
            override fun dataTestName(): String = "$description の場合、例外が飛ぶ"
        }

        withData(
            TestCase(description = "年のみ", arguments = mapOf("amount" to 1000L, "year" to 2026L)),
            TestCase(description = "月のみ", arguments = mapOf("amount" to 1000L, "month" to 8L)),
            TestCase(description = "年 + 日", arguments = mapOf("amount" to 1000L, "year" to 2026L, "day" to 3L)),
            TestCase(description = "年月", arguments = mapOf("amount" to 1000L, "year" to 2026L, "month" to 8L)),
        ) { (_, arguments) ->
            // setup
            val interactionCommand = mockk<InteractionCommand> {
                every { integers } returns arguments
            }

            // execute & assert
            shouldThrowExactly<IllegalArgumentException> { CreateExpenseRequest.from(interactionCommand = interactionCommand) }
                .message shouldBe "日付は日のみ、月日、年月日のいずれかで入力する必要がある。"
        }
    }
})
