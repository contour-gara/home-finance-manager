package org.contourgara.eventlistener

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.accumulate
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import dev.kord.core.cache.data.EmbedFieldData

object RegisterBillValidation {
    fun validateAmount(amount: Int): Either<NonEmptyList<RegisterBillValidationError>, Unit> = either {
        accumulate {
            ensureOrAccumulate(amount >= 1) { RegisterBillValidationError.AmountError.of(amount) }
            Unit.right()
        }
    }

    fun validateClaimant(name: String): Either<RegisterBillValidationError, Unit> = either {
        ensure(listOf("gara", "yuki").contains(name)) { RegisterBillValidationError.ClaimantError.of(name) }
        Unit.right()
    }

    fun validateMemo(memo: String): Either<RegisterBillValidationError, Unit> = either {
        ensure(memo.isBlank()) { RegisterBillValidationError.MemoError.of(memo) }
        Unit.right()
    }

    private fun validateEmbedFieldDataes(embedFieldDataes: List<EmbedFieldData>): Either<Exception, Unit> =
        either {
            ensure(embedFieldDataes.map { it.name } == listOf("請求金額だっピ")) { RuntimeException("キーが不正: ${embedFieldDataes.map { it.name }}") }
            Unit.right()
        }

    sealed interface RegisterBillValidationError {
        val message: String
        val dataPath: String

        @ConsistentCopyVisibility
        data class AmountError private constructor(override val message: String, override val dataPath: String= "amount") : RegisterBillValidationError {
            companion object {
                fun of(invalidAmount: Int): AmountError = AmountError("請求金額は 1 円以上 Int の最大値以下ではならない: $invalidAmount")
            }
        }

        @ConsistentCopyVisibility
        data class ClaimantError internal constructor(override val message: String, override val dataPath: String= "claimant") : RegisterBillValidationError {
            companion object {
                fun of(invalidName: String): ClaimantError = ClaimantError("請求者は gara か yuki でないとならない: $invalidName")
            }
        }

        @ConsistentCopyVisibility
        data class MemoError internal constructor(override val message: String, override val dataPath: String= "memo") : RegisterBillValidationError {
            companion object {
                fun of(invalidMemo: String): MemoError = MemoError("メモは空文字や空白のみではならない: $invalidMemo")
            }
        }
    }
}
