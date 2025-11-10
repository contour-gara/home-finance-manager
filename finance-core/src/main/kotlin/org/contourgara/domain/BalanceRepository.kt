package org.contourgara.domain

interface BalanceRepository {
    fun save(balance: Balance)
    fun findLatest(lender: User, borrower: User): Balance
}
