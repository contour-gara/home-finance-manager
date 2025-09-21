package org.contourgara.eventlistener

import dev.kord.common.Color
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.cache.data.EmbedFieldData
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldHaveSize
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe
import org.contourgara.eventlistener.RegisterBillValidation.validateAmount
import org.contourgara.eventlistener.RegisterBillValidation.validateBorrower
import org.contourgara.eventlistener.RegisterBillValidation.validateEmbedData
import org.contourgara.eventlistener.RegisterBillValidation.validateLender
import org.contourgara.eventlistener.RegisterBillValidation.validateLenderAndBorrower
import org.contourgara.eventlistener.RegisterBillValidation.validateMemo
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.AmountError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.EmbedDataColorError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.EmbedDataFieldAmountFormatError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.EmbedDataFieldNamesError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.EmbedDataTitleError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.BorrowerError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.LenderError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.LenderAndBorrowerError
import org.contourgara.eventlistener.RegisterBillValidation.RegisterBillValidationError.MemoError

class RegisterBillValidationTest : FunSpec({
    context("請求金額のバリデーション") {
        test("請求金額が正しい場合、Right を返す") {
            // setup
            val amount = "1"

            // execute
            val actual = validateAmount(amount)

            // assert
            actual.shouldBeRight()
        }

        data class InvalidAmountTestCase(val amount: String) : WithDataTestName {
            override fun dataTestName(): String = "請求金額が $amount の場合、AmountError を返す"
        }

        withData(
            InvalidAmountTestCase("0"),
            InvalidAmountTestCase("2147483648"),
            InvalidAmountTestCase("文字"),
        ) { (amount) ->
            // execute
            val actual = validateAmount(amount)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe AmountError.of(amount)
            }
        }
    }

    context("メモのバリデーション") {
        test("メモが空でない場合、Right を返す") {
            // setup
            val memo = "test"

            // execute
            val actual = validateMemo(memo)

            // assert
            actual.shouldBeRight()
        }

        data class InvalidMemoTestCase(val memo: String) : WithDataTestName {
            override fun dataTestName(): String = "メモが '$memo' の場合、MemoError を返す"
        }

        withData(
            InvalidMemoTestCase(""),
            InvalidMemoTestCase(" "),
            InvalidMemoTestCase("　"),
        ) { (memo) ->
            // execute
            val actual = validateMemo(memo)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe MemoError.of(memo)
            }
        }
    }

    context("請求者のバリデーション") {
        data class ValidLenderTestCase(val lender: User) : WithDataTestName {
            override fun dataTestName(): String = "請求者が $lender の場合、Right を返す"
        }

        withData(
            ValidLenderTestCase(User.GARA),
            ValidLenderTestCase(User.YUKI),
        ) { (lender) ->
            // execute
            val actual = validateLender(lender)

            // assert
            actual.shouldBeRight()
        }

        test("請求者が UNDEFINED の場合、LenderError を返す") {
            // setup
            val input = User.UNDEFINED

            // execute
            val actual = validateLender(input)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe LenderError.of(input)
            }
        }
    }

    context("請求先のバリデーション") {
        data class ValidBorrowerTestCase(val borrower: User) : WithDataTestName {
            override fun dataTestName(): String = "請求先が $borrower の場合、Right を返す"
        }

        withData(
            ValidBorrowerTestCase(User.GARA),
            ValidBorrowerTestCase(User.YUKI),
        ) { (borrower) ->
            // execute
            val actual = validateBorrower(borrower)

            // assert
            actual.shouldBeRight()
        }

        test("請求先が UNDEFINED の場合、BorrowerError を返す") {
            // setup
            val input = User.UNDEFINED

            // execute
            val actual = validateBorrower(input)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe BorrowerError.of(input)
            }
        }
    }

    context("請求者と請求先の組み合わせバリデーション") {
        data class ValidLenderAndBorrowerTestCase(val lender: User, val borrower: User) : WithDataTestName {
            override fun dataTestName(): String = "請求者と請求先が異なる場合、Right を返す: $lender, $borrower"
        }

        withData(
            ValidLenderAndBorrowerTestCase(User.YUKI, User.GARA),
            ValidLenderAndBorrowerTestCase(User.GARA, User.YUKI),
        ) { (lender, borrower) ->
            // execute
            val actual = validateLenderAndBorrower(lender, borrower)

            // assert
            actual.shouldBeRight()
        }

        data class InvalidLenderAndBorrowerTestCase(val lender: User, val borrower: User) : WithDataTestName {
            override fun dataTestName(): String = "請求者と請求先が同じ場合、LenderAndBorrowerError を返す: $lender, $borrower"
        }

        withData(
            InvalidLenderAndBorrowerTestCase(User.YUKI, User.YUKI),
            InvalidLenderAndBorrowerTestCase(User.GARA, User.GARA),
        ) { (lender, borrower) ->
            // execute
            val actual = validateLenderAndBorrower(lender, borrower)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe LenderAndBorrowerError.of(lender, borrower)
            }
        }
    }

    context("EmbedData のバリデーション") {
        test("EmbedData が正しい場合、Right を返す") {
            // setup
            val embedData = EmbedData(
                title = Optional.Value("入力情報だっピ"),
                color = OptionalInt.Value(Color(0, 255, 0).rgb),
                fields = Optional.Value(
                    listOf(
                        EmbedFieldData(name = "申請 ID", inline = OptionalBoolean.Value(true), value = "ID"),
                        EmbedFieldData(name = "請求金額", inline = OptionalBoolean.Value(true), value = "1 円"),
                        EmbedFieldData(name = "請求者", inline = OptionalBoolean.Value(true), value = "yuki"),
                        EmbedFieldData(name = "請求先", inline = OptionalBoolean.Value(true), value = "gara"),
                        EmbedFieldData(name = "メモ", inline = OptionalBoolean.Value(true), value = "test"),
                    )
                )
            )

            // execute
            val actual = validateEmbedData(embedData)

            // assert
            actual.shouldBeRight()
        }

        test("EmbedData の各項目が不正な場合、期待するエラーを返す") {
            // setup
            val embedData = EmbedData(
                title = Optional.Value("入力情報"),
                color = OptionalInt.Value(Color(255, 255, 50).rgb),
                fields = Optional.Value(emptyList())
            )

            // execute
            val actual = validateEmbedData(embedData)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 3
                actual.value shouldBe listOf(
                    EmbedDataTitleError.of("入力情報"),
                    EmbedDataColorError.of(Color(255, 255, 50), "緑色"),
                    EmbedDataFieldNamesError.of(emptyList(), listOf("申請 ID", "請求金額", "請求者", "請求先", "メモ")),
                )
            }
        }

        test("EmbedData の請求金額フォーマットが不正な場合、期待するエラーを返す") {
            // setup
            val embedData = EmbedData(
                title = Optional.Value("入力情報だっピ"),
                color = OptionalInt.Value(Color(0, 255, 0).rgb),
                fields = Optional.Value(
                    listOf(
                        EmbedFieldData(name = "申請 ID", inline = OptionalBoolean.Value(true), value = "ID"),
                        EmbedFieldData(name = "請求金額", inline = OptionalBoolean.Value(true), value = "1円"),
                        EmbedFieldData(name = "請求者", inline = OptionalBoolean.Value(true), value = "yuki"),
                        EmbedFieldData(name = "請求先", inline = OptionalBoolean.Value(true), value = "gara"),
                        EmbedFieldData(name = "メモ", inline = OptionalBoolean.Value(true), value = "test"),
                    )
                )
            )

            // execute
            val actual = validateEmbedData(embedData)

            // assert
            assertSoftly {
                actual.shouldBeLeft()
                actual.value shouldHaveSize 1
                actual.value.first() shouldBe EmbedDataFieldAmountFormatError.of("1円")
            }
        }
    }
})
