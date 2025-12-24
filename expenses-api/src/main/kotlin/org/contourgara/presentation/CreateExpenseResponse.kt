package org.contourgara.presentation

import kotlinx.serialization.Serializable
import org.contourgara.application.CreateExpenseDto
import ulid.ULID

@Serializable
data class CreateExpenseResponse(
    private val expenseId: ULID,
    private val expenseEventId: ULID,
) {
    companion object {
        fun from(createExpenseDto: CreateExpenseDto): CreateExpenseResponse =
            CreateExpenseResponse(
                expenseId = createExpenseDto.expenseId,
                expenseEventId = createExpenseDto.expenseEventId,
            )
    }
}
