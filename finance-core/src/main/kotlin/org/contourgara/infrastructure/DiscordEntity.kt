package org.contourgara.infrastructure

import dev.kord.common.entity.Snowflake
import org.contourgara.domain.RegisterBill
import org.contourgara.domain.User
import ulid.ULID

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
