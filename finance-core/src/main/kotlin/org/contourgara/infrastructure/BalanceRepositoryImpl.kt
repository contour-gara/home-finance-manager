package org.contourgara.infrastructure

import org.contourgara.domain.Balance
import org.contourgara.domain.BalanceRepository
import org.contourgara.domain.User
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
open class BalanceRepositoryImpl(
    private val jdbcClient: JdbcClient,
) : BalanceRepository {
    override fun save(balance: Balance) =
        balance
            .toEntity()
            .also {
                jdbcClient
                    .sql("INSERT INTO balance (balance_id, lender, borrower, amount, last_event_id) VALUES (?, ?, ?, ?, ?)")
                    .params(it.balanceId, it.lender, it.borrower, it.amount, it.lastEventId)
                    .update()
            }
            .let { Unit }

    override fun findLatest(
        lender: User,
        borrower: User,
    ): Balance =
        jdbcClient
            .sql("SELECT balance_id, lender, borrower, amount, last_event_id FROM balance WHERE lender = ? AND borrower = ? ORDER BY balance_id DESC")
            .params(lender.name, borrower.name)
            .query(DataClassRowMapper(BalanceEntity::class.java))
            .list()
            .firstOrNull()
            ?.toModel()
            ?: Balance.noRecord(lender, borrower)
}
