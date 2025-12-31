package org.contourgara.domain

data class Expenses(
    val lastEventId: ExpenseEventId,
    val year: Year,
    val month: Month,
    val payer: Payer,
    val category: Category,
    val amount: Amount,
) {
    companion object {
        fun from(expenses: Expenses?, expense: Expense, expenseEventId: ExpenseEventId) : Expenses =
            expenses
                ?.also {
                    require(expenses.lastEventId < expenseEventId) { "expenseEventId must be greater than lastEventId: expenseEventId = ${expenseEventId.value}, lastEventId = ${expenses.lastEventId.value}" }
                    require(expenses.year == expense.year) { "year must be same: expenses.year = ${expenses.year.intYear}, expense.year = ${expense.year.intYear}" }
                    require(expenses.month == expense.month) { "month must be same: expenses.month = ${expenses.month.intMonth}, expense.month = ${expense.month.intMonth}" }
                    require(expenses.payer == expense.payer) { "payer must be same: expenses.payer = ${expenses.payer.name}, expense.payer = ${expense.payer.name}" }
                    require(expenses.category == expense.category) { "category must be same: expenses.category = ${expenses.category.name}, expense.category = ${expense.category.name}" }
                } ?.let {
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
