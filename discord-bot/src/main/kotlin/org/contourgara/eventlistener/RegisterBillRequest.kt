package org.contourgara.eventlistener

import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.EmbedData
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.constraints.enum
import io.konform.validation.constraints.maximum
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.minimum
import io.konform.validation.constraints.notBlank

@ConsistentCopyVisibility
data class RegisterBillRequest private constructor(
    val amount: Int,
    val claimant: User = User.UNDEFINED,
    val memo: String = ""
) {
    companion object {
        private val AMOUNT_CHECK = Validation<Int> {
            minimum(1) hint "請求金額は 1 円未満ではならない"
            maximum(Int.MAX_VALUE)  hint "請求金額は Int の最大値円超過ではならない"
        }

        private val CLAIMANT_CHECK = Validation<User> {
            enum(User.GARA, User.YUKI) hint "請求者は gara か yuki でないとならない"
        }

        private val MEMO_CHECK = Validation<String> {
            notBlank() hint "メモは空白のみではならない"
            minLength(1) hint "メモは 1 文字未満ではならない"
        }

        fun of(amount: Int): ValidationResult<RegisterBillRequest> =
            Validation {
                RegisterBillRequest::amount {
                    run(AMOUNT_CHECK)
                }
            }(RegisterBillRequest(amount = amount))

        fun of(embedData: EmbedData, userId: Long, memo: String): ValidationResult<RegisterBillRequest> =
            Validation {
                RegisterBillRequest::amount {
                    run(AMOUNT_CHECK)
                }

                RegisterBillRequest::claimant {
                    run(CLAIMANT_CHECK)
                }

                RegisterBillRequest::memo {
                    run(MEMO_CHECK)
                }
            }(RegisterBillRequest(
                amount = embedData.fields.orEmpty().first().value.split(" ").first().toInt(),
                claimant = User.of(userId),
                memo = memo
            ))
    }
}
