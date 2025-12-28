package org.contourgara.infrastructure.repository

import org.contourgara.domain.Category
import org.contourgara.domain.Expenses
import org.contourgara.domain.Month
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpensesRepository

object ExpensesRepositoryImpl : ExpensesRepository {
    override fun findLatestExpenses(
        year: Year,
        month: Month,
        payer: Payer,
        category: Category
    ): Expenses? {
        TODO("Not yet implemented")
    }
}
