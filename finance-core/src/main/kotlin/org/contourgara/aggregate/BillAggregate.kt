package org.contourgara.aggregate

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import org.contourgara.domain.Bill
import org.contourgara.domain.BillId

@Aggregate
class BillAggregate() {
    @AggregateIdentifier private lateinit var billId: BillId
    private lateinit var bill: Bill

    @CommandHandler
    constructor(command: RegisterBillCommand) : this() {
        println("Start register command")
        AggregateLifecycle.apply(RegisterBillEvent.from(command))
        println("End register command")
    }

    @EventSourcingHandler
    fun handle(event: RegisterBillEvent) {
        println("Start register event")
        billId = event.bill.billId
        bill = event.bill
        println("End register event")
    }

    @CommandHandler
    fun handle(command: DeleteBillCommand) {
        println("Start delete command")
        AggregateLifecycle.apply(DeleteBillEvent(billId, bill))
        println("End delete command")
    }

    @EventSourcingHandler
    fun handle(event: DeleteBillEvent) {
        println("Start delete event")
        AggregateLifecycle.markDeleted()
        println("End delete event")
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
    val bill: Bill,
)
