package org.contourgara.domain

data class Expenses(
    val lastEventId: ExpenseEventId,
    val year: Year,
    val month: Month,
    val payer: Payer,
    val category: Category,
    val amount: Int,
) {
    companion object {
        fun from(expenses: Expenses?, expense: Expense, expenseEventId: ExpenseEventId) : Expenses =
            expenses
                ?.let {
                    expenses.copy(
                        lastEventId = expenseEventId,
                        amount = it.amount + expense.amount,
                    )
                } ?:
                Expenses(
                    lastEventId = expenseEventId,
                    year = expense.year,
                    month = expense.month,
                    payer = expense.payer,
                    category = expense.category,
                    amount = expense.amount,
                )
    }
}
