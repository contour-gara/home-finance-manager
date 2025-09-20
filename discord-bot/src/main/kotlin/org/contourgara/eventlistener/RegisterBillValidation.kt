package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import dev.kord.common.Color
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.EmbedData

@OptIn(ExperimentalRaiseAccumulateApi::class)
object RegisterBillValidation {
    fun validateAmount(amount: Int): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        either {
            accumulate {
                ensureOrAccumulate(amount >= 1) { RegisterBillValidationError.AmountError.of(amount) }
            }
            Unit.right()
        }

    fun validateLender(lender: User): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        either {
            accumulate {
                ensureOrAccumulate(
                    listOf(
                        User.GARA,
                        User.YUKI
                    ).contains(lender)
                ) { RegisterBillValidationError.LenderError.of(lender) }
            }
            Unit.right()
        }

    fun validateBorrower(borrower: User): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        either {
            accumulate {
                ensureOrAccumulate(
                    listOf(
                        User.GARA,
                        User.YUKI
                    ).contains(borrower)
                ) { RegisterBillValidationError.BorrowerError.of(borrower) }
            }
            Unit.right()
        }

    fun validateLenderAnfBorrower(lender: User, borrower: User): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        either {
            accumulate {
                ensureOrAccumulate(
                    lender != borrower
                ) { RegisterBillValidationError.LenderAndBorrowerError.of(lender, borrower) }
            }
            Unit.right()
        }

    fun validateMemo(memo: String): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        either {
            accumulate {
                ensureOrAccumulate(memo.isNotBlank()) { RegisterBillValidationError.MemoError.of(memo) }
            }
            Unit.right()
        }

    fun validateAmountEmbedData(embedData: EmbedData): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        either {
            accumulate {
                validateEmbedDataTitle(embedData).bindOrAccumulate()
                validateEmbedDataStatusProgress(embedData).bindOrAccumulate()
                validateAmountEmbedDataField(embedData).bindOrAccumulate()
            }
            validateAmountEmbedDataFieldFormat(embedData).bind()
            Unit.right()
        }

    fun validateEmbedData(embedData: EmbedData): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        either {
            accumulate {
                validateEmbedDataTitle(embedData).bindOrAccumulate()
                validateEmbedDataStatusComplete(embedData).bindOrAccumulate()
                validateEmbedDataField(embedData).bindOrAccumulate()
            }
            validateAmountEmbedDataFieldFormat(embedData).bind()
            Unit.right()
        }

    private fun validateEmbedDataTitle(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        embedData.title.value.let {
            either {
                ensure(it == "入力情報だっピ") { RegisterBillValidationError.EmbedDataTitleError.of(it) }
                Unit.right()
            }
        }

    private fun validateEmbedDataStatusProgress(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        Color(embedData.color.asNullable ?: 0).let {
            either {
                ensure(it == Color(255, 255, 50)) { RegisterBillValidationError.EmbedDataColorError.of(it, "黄色") }
                Unit.right()
            }
        }

    private fun validateEmbedDataStatusComplete(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        Color(embedData.color.asNullable ?: 0).let {
            either {
                ensure(it == Color(0, 255, 0)) { RegisterBillValidationError.EmbedDataColorError.of(it, "緑色") }
                Unit.right()
            }
        }

    private fun validateAmountEmbedDataField(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        embedData.fields.orEmpty().map {
            it.name
        }.let {
            either {
                val validEmbedDataFieldNames = listOf("請求金額")
                ensure(it == validEmbedDataFieldNames) { RegisterBillValidationError.EmbedDataFieldNamesError.of(it, validEmbedDataFieldNames) }
                Unit.right()
            }
        }

    private fun validateEmbedDataField(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        embedData.fields.orEmpty().map {
            it.name
        }.let {
            either {
                val validEmbedDataFieldNames = listOf("申請 ID", "請求金額", "請求者", "請求先", "メモ")
                ensure(it == validEmbedDataFieldNames) { RegisterBillValidationError.EmbedDataFieldNamesError.of(it, validEmbedDataFieldNames) }
                Unit.right()
            }
        }

    private fun validateAmountEmbedDataFieldFormat(embedData: EmbedData): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        embedData.fields.orEmpty().associate {
            it.name to it.value
        }.let {
            it["請求金額"]!!
        }.let {
            either {
                accumulate {
                    ensureOrAccumulate(Regex("""^\d{1,3}(,\d{3})*\s円$""").matches(it)) { RegisterBillValidationError.EmbedDataFieldAmountFormatError.of(it) }
                }
                Unit.right()
            }
        }

    sealed interface RegisterBillValidationError {
        val message: String
        val dataPath: String

        @ConsistentCopyVisibility
        data class AmountError private constructor(
            override val message: String,
            override val dataPath: String = "amount"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidAmount: Int): AmountError =
                    AmountError("請求金額は 1 円以上 Int の最大値以下ではならない: $inValidAmount")
            }
        }

        @ConsistentCopyVisibility
        data class LenderError internal constructor(
            override val message: String,
            override val dataPath: String = "lender"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidLender: User): LenderError =
                    LenderError("請求者は gara か yuki でないとならない: ${inValidLender.name.lowercase()}")
            }
        }

        @ConsistentCopyVisibility
        data class BorrowerError internal constructor(
            override val message: String,
            override val dataPath: String = "borrower"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidBorrower: User): BorrowerError =
                    BorrowerError("請求先は gara か yuki でないとならない: ${inValidBorrower.name.lowercase()}")
            }
        }

        @ConsistentCopyVisibility
        data class LenderAndBorrowerError internal constructor(
            override val message: String,
            override val dataPath: String = "lender, borrower"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidLender: User, inValidBorrower: User): LenderAndBorrowerError =
                    LenderAndBorrowerError("請求者と請求先が同じではならない: ${inValidLender.name.lowercase()}, ${inValidBorrower.name.lowercase()}")
            }
        }

        @ConsistentCopyVisibility
        data class MemoError internal constructor(
            override val message: String,
            override val dataPath: String = "memo"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidMemo: String): MemoError = MemoError("メモは空文字や空白のみではならない: $inValidMemo")
            }
        }

        @ConsistentCopyVisibility
        data class EmbedDataTitleError internal constructor(
            override val message: String,
            override val dataPath: String = "EmbedData.title"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidTitle: String?): EmbedDataTitleError =
                    EmbedDataTitleError("EmbedData のタイトルは '入力情報だっピ' でないとならない: $inValidTitle")
            }
        }

        @ConsistentCopyVisibility
        data class EmbedDataColorError internal constructor(
            override val message: String,
            override val dataPath: String = "EmbedData.color"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidColor: Color, validColor: String): EmbedDataColorError =
                    EmbedDataColorError("EmbedData のカラーは $validColor でないとならない: $inValidColor")
            }
        }

        @ConsistentCopyVisibility
        data class EmbedDataFieldNamesError internal constructor(
            override val message: String,
            override val dataPath: String = "EmbedData.field.names"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidEmbedDataFieldNames: List<String>, validEmbedDataFieldNames: List<String>): EmbedDataFieldNamesError =
                    EmbedDataFieldNamesError("EmbedData のフィールド名は $validEmbedDataFieldNames でないとならない: $inValidEmbedDataFieldNames")
            }
        }

        @ConsistentCopyVisibility
        data class EmbedDataFieldAmountFormatError internal constructor(
            override val message: String,
            override val dataPath: String = "EmbedData.field.value"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidFormatAmount: String): EmbedDataFieldAmountFormatError =
                    EmbedDataFieldAmountFormatError("請求金額のフォーマットは 'x 円' でないとならない: $inValidFormatAmount")
            }
        }
    }
}
