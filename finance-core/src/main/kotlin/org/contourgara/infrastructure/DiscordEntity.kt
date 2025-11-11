package org.contourgara.infrastructure

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.Debt
import org.contourgara.domain.Loan
import org.contourgara.domain.RegisterBill
import org.contourgara.domain.User
import ulid.ULID
import kotlin.text.trim

data class RegisterBillEntity(
    val billId: ULID,
    val eventId: String,
    val lender: UserEntity,
    val borrower: UserEntity,
) {
    companion object {
        fun from(registerBill: RegisterBill): RegisterBillEntity =
            RegisterBillEntity(
                billId = registerBill.billId,
                eventId = registerBill.eventId,
                lender = UserEntity.from(registerBill.lender),
                borrower = UserEntity.from(registerBill.borrower),
            )
    }
}

data class LoanEntity(
    val lender: UserEntity,
    val borrower: UserEntity,
    val amount: Int,
    val lastEventId: String,
) {
    companion object {
        fun from(loan: Loan): LoanEntity =
            LoanEntity(
                lender = UserEntity.from(loan.lender),
                borrower = UserEntity.from(loan.borrower),
                amount = loan.amount,
                lastEventId = loan.lastEventId,
            )
    }

    val displayAmount: String get() = amount.formatAmount()
}

data class DebtEntity(
    val lender: UserEntity,
    val borrower: UserEntity,
    val amount: Int,
    val lastEventId: String,
) {
    companion object {
        fun from(debt: Debt): DebtEntity =
            DebtEntity(
                lender = UserEntity.from(debt.lender),
                borrower = UserEntity.from(debt.borrower),
                amount = debt.amount,
                lastEventId = debt.lastEventId,
            )
    }

    val displayAmount: String get() = amount.formatAmount()
}

enum class UserEntity(
    val id: Snowflake,
) {
    GARA(Snowflake(703805458116509818)),
    YUKI(Snowflake(889339009061507143)),
    ;

    companion object {
        fun from(user: User): UserEntity = valueOf(user.name)
    }
}

private fun Int.formatAmount(): String =
    toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
        .let { " $it 円" }
