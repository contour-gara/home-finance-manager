package org.contourgara.domain

data class MonthlyExpenses(
    val values: Map<Category, Amount>,
) {
    companion object {
        fun from(expenses: List<Expenses>): MonthlyExpenses =
            expenses
                .also {
                    require(
                        value = expenses.distinctBy { it.year to it.month }.size == 1
                    ) {
                        "異なる年のデータがあります。"
                    }
                }
                .groupBy { it.category to it.payer }
                .mapValues { (_, expensesList) ->
                    expensesList.maxBy { it.lastEventId.value }
                }
                .values
                .groupBy { it.category }
                .mapValues { (_, expensesList) ->
                    expensesList
                        .map { it.amount }
                        .reduce { acc, amount -> acc + amount }
                }
                .let { MonthlyExpenses(values = it) }
    }
}
