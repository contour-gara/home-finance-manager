package org.contourgara.domain

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
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

class UlidDeserializer : JsonDeserializer<ULID>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ULID =
        ULID.parseULID(p.valueAsString)
}
