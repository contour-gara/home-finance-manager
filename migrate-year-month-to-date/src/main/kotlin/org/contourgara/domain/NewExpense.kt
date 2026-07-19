package org.contourgara.domain

import kotlinx.datetime.LocalDate

data class NewExpense(
    val id: String,
    val date: LocalDate,
) {
    companion object {
        fun from(oldExpense: OldExpense): NewExpense =
            NewExpense(
                id = oldExpense.id,
                date = oldExpense.dateFromMemo(),
            )
    }
}
