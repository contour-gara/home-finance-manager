package org.contourgara.domain.infrastructure

import org.contourgara.domain.Category
import org.contourgara.domain.Expenses
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year

interface ExpensesRepository {
    fun findLatestExpenses(year: Year, month: Month, payer: Payer, category: Category): Expenses?
    fun findMonthlyExpenses(year: Year, month: Month): List<Expenses>
    fun save(expenses: Expenses): Expenses
}
