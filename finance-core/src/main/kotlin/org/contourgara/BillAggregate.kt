package org.contourgara

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import ulid.ULID

@Aggregate
class BillAggregate() {
    @AggregateIdentifier private lateinit var billId: BillId
    private lateinit var bill: Bill

    @CommandHandler
    constructor(command: RegisterBillCommand) : this() {
        AggregateLifecycle.apply(RegisterBillEvent.from(command))
        println("Execute command")
    }

    @EventSourcingHandler
    fun on(event: RegisterBillEvent) {
        billId = event.bill.billId
        bill = event.bill
        println("Apply event")
    }

    @CommandHandler
    fun handle(command: DeleteBillCommand) {
        AggregateLifecycle.apply(DeleteBillEvent.from(command))
        println("Execute command")
    }

    @EventSourcingHandler
    fun on(event: DeleteBillEvent) {
        AggregateLifecycle.markDeleted()
        println("Apply event")
    }
}

data class RegisterBillCommand(
    val bill: Bill,
)

data class RegisterBillEvent(
    val bill: Bill,
) {
    companion object {
        fun from(command: RegisterBillCommand): RegisterBillEvent =
            RegisterBillEvent(
                bill = command.bill,
            )
    }
}

data class DeleteBillCommand(
    @TargetAggregateIdentifier val billId: BillId,
)

data class DeleteBillEvent(
    val billId: BillId,
) {
    companion object {
        fun from(command: DeleteBillCommand): DeleteBillEvent =
            DeleteBillEvent(
                billId = command.billId,
            )
    }
}

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

class UlidDeserializer : JsonDeserializer<ULID>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ULID =
        ULID.parseULID(p.valueAsString)
}
