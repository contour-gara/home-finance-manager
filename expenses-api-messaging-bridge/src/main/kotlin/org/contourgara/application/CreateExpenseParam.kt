package org.contourgara.application

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.Expense
import org.contourgara.domain.ExpenseId
import org.contourgara.domain.MessageId
import ulid.ULID

data class CreateExpenseParam(
    private val messageId: String,
    private val expenseId: String,
    private val amount: Int,
    private val payer: String,
    private val category: String,
    private val year: Int,
    private val month: Int,
    private val memo: String,
) {
    fun toModel(): Pair<MessageId, Expense> =
        Pair(
            first = MessageId(value = Snowflake(value = messageId)),
            second = Expense(
                expenseId = ExpenseId(value = ULID.parseULID(ulidString = expenseId)),
                amount = amount,
                payer = payer,
                category = category,
                year = year,
                month = month,
                memo = memo,
            )
        )
}
