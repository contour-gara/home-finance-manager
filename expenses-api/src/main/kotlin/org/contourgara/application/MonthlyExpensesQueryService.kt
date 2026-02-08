package org.contourgara.application

import arrow.core.Either
import org.contourgara.domain.Month
import org.contourgara.domain.MonthlyExpenses
import org.contourgara.domain.Payer
import org.contourgara.domain.Year
import org.contourgara.domain.infrastructure.ExpensesRepository
import org.contourgara.domain.toException

class MonthlyExpensesQueryService(
    private val expensesRepository: ExpensesRepository,
) {
    fun execute(year: Int, month: Int, payer: String? = null): Map<String, Int> =
        expensesRepository
            .findMonthlyExpenses(year = Year.of(value = year), month = Month.of(value = month))
            .let {
                if (payer == null) MonthlyExpenses.from(expenses = it)
                else when (val result = Payer.of(value = payer)) {
                    is Either.Right -> MonthlyExpenses.from(expenses = it, payer = result.value)
                    is Either.Left -> throw result.value.toException()
                }
            }
            .values
            .entries
            .associate { (category, amount) ->
                category.name to amount.value
            }
}
