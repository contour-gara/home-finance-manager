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

    fun validateClaimant(userId: Long): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        User.of(userId).let {
            either {
                accumulate {
                    ensureOrAccumulate(
                        listOf(
                            User.GARA,
                            User.YUKI
                        ).contains(it)
                    ) { RegisterBillValidationError.ClaimantError.of(it) }
                }
                Unit.right()
            }
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
                validateEmbedDataStatus(embedData).bindOrAccumulate()
                validateAmountEmbedDataField(embedData).bindOrAccumulate()
            }
            validateAmountEmbedDataFieldFormat(embedData).bind()
            validateAmount(embedData.fields.orEmpty().first().value.split(" ").first().toInt()).bind()
            Unit.right()
        }

    private fun validateEmbedDataTitle(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        embedData.title.value.let {
            either {
                ensure(it == "入力情報だっピ") { RegisterBillValidationError.EmbedDataTitleError.of(it) }
                Unit.right()
            }
        }

    private fun validateEmbedDataStatus(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        Color(embedData.color.asNullable ?: 0).let {
            either {
                ensure(it == Color(255, 255, 50)) { RegisterBillValidationError.EmbedDataColorError.of(it) }
                Unit.right()
            }
        }

    private fun validateAmountEmbedDataField(embedData: EmbedData): Either<RegisterBillValidationError, Unit> =
        embedData.fields.orEmpty().map {
            it.name
        }.let {
            either {
                ensure(it == listOf("請求金額だっピ")) { RegisterBillValidationError.EmbedDataFieldNamesError.of(it) }
                Unit.right()
            }
        }

    private fun validateAmountEmbedDataFieldFormat(embedData: EmbedData): Either<NonEmptyList<RegisterBillValidationError>, Unit> =
        embedData.fields.orEmpty().first().value.let {
            either {
                accumulate {
                    ensureOrAccumulate(Regex("""^\d+\s円$""").matches(it)) { RegisterBillValidationError.AmountFormatError.of(it) }
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
        data class ClaimantError internal constructor(
            override val message: String,
            override val dataPath: String = "claimant"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidUser: User): ClaimantError =
                    ClaimantError("請求者は gara か yuki でないとならない: ${inValidUser.name.lowercase()}")
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
                fun of(inValidColor: Color): EmbedDataColorError =
                    EmbedDataColorError("EmbedData のカラーは黄色でないとならない: $inValidColor")
            }
        }

        @ConsistentCopyVisibility
        data class EmbedDataFieldNamesError internal constructor(
            override val message: String,
            override val dataPath: String = "EmbedData.field.names"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidEmbedDataFieldNames: List<String>): EmbedDataFieldNamesError =
                    EmbedDataFieldNamesError("EmbedData のフィールド名は [請求金額だっピ] でないとならない: $inValidEmbedDataFieldNames")
            }
        }

        @ConsistentCopyVisibility
        data class AmountFormatError internal constructor(
            override val message: String,
            override val dataPath: String = "EmbedData.field.value"
        ) : RegisterBillValidationError {
            companion object {
                fun of(inValidFormatAmount: String): AmountFormatError =
                    AmountFormatError("請求金額のフォーマットは 'x 円' でないとならない: $inValidFormatAmount")
            }
        }
    }
}
