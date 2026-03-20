package org.contourgara.domain

import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.annotation.JsonDeserialize
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer
import ulid.ULID

data class BillId(
    @field:JsonSerialize(using = ToStringSerializer::class)
    @field:JsonDeserialize(using = UlidDeserializer::class)
    val value: ULID,
)

data class Bill(
    val billId: BillId,
    val amount: Int,
    val lender: User,
    val borrower: User,
    val memo: String,
) {
    companion object {
        fun of(
            billId: ULID,
            amount: Int,
            lender: User,
            borrower: User,
            memo: String,
        ): Bill =
            Bill(
                billId = BillId(billId),
                amount = amount,
                lender = lender,
                borrower = borrower,
                memo = memo,
            )
    }
}

enum class User {
    GARA,
    YUKI,
    ;
}

class UlidDeserializer : ValueDeserializer<ULID>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ULID =
        ULID.parseULID(p.valueAsString)
}
