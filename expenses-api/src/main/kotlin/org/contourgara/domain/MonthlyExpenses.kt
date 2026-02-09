package org.contourgara.domain

data class MonthlyExpenses(
    val values: Map<Category, Amount>,
) {
    companion object {
        fun from(expenses: List<Expenses>, payer: Payer? = null): MonthlyExpenses =
            expenses
                .let {
                    if (payer != null) it.filter { it.payer == payer }
                    else it
                }
                .also {
                    require(
                        value = expenses.distinctBy { it.year to it.month }.size in listOf(0, 1)
                    ) {
                        "異なる年月のデータがあります。"
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
