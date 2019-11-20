package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.CreateOrderCommand;
import nl.trifork.coins.coreapi.ExecuteOrderCommand;
import nl.trifork.coins.coreapi.FailOrderCommand;
import nl.trifork.coins.coreapi.OrderCreatedEvent;
import nl.trifork.coins.coreapi.OrderExecutedEvent;
import nl.trifork.coins.coreapi.OrderFailedEvent;
import nl.trifork.coins.coreapi.OrderStatus;
import nl.trifork.coins.coreapi.OrderSuccessEvent;
import nl.trifork.coins.coreapi.SuccessOrderCommand;
import nl.trifork.model.CoinType;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

import static nl.trifork.coins.coreapi.OrderStatus.COMPLETED;
import static nl.trifork.coins.coreapi.OrderStatus.CREATED;
import static nl.trifork.coins.coreapi.OrderStatus.FAILED;
import static nl.trifork.coins.coreapi.OrderStatus.PENDING;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String id;
    private String userId;
    private CoinType fromCurrency;
    private CoinType toCurrency;
    private BigDecimal amount;
    private BigDecimal price;
    private OrderStatus status;

    public OrderAggregate() {
    }

    @CommandHandler
    //FIXME Exercise 8: send an event for this command to provide state for this aggregate through event sourcing
    public OrderAggregate(CreateOrderCommand command) {

    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.id = event.getId();
        this.userId = event.getUserId();
        this.fromCurrency = event.getFromCurrency();
        this.toCurrency = event.getToCurrency();
        this.amount = event.getAmount();
        this.price = event.getPrice();
        this.status = CREATED;
    }

    @CommandHandler
    //FIXME Exercise 9: validate the command and send an appropriate event or throw an exception
    public String executeOrder(ExecuteOrderCommand command) {
        if (!command.getUserId().equals(this.userId)) {
           //TODO: implement
        }
        if (!this.status.equals(CREATED)) {
            //TODO: implement
        } else {
            //TODO: implement
        }
        return command.getId();
    }

    @EventSourcingHandler
    public void on(OrderExecutedEvent event) {
        this.status = PENDING;
    }

    @CommandHandler
    //FIXME Exercise 10: validate the command and send an appropriate event or throw an exception
    public void success(SuccessOrderCommand command) {
        if (!this.status.equals(PENDING)) {
           //TODO: implement
        } else {
            //TODO: implement
        }
    }

    @EventSourcingHandler
    public void on(OrderSuccessEvent event) {
        this.status = COMPLETED;
    }

    @CommandHandler
    public void fail(FailOrderCommand command) {
        if (!this.status.equals(PENDING)) {
            throw new IllegalStateException("The status of this order is not valid");
        } else {
            apply(new OrderFailedEvent(command.getId()));
        }
    }

    @EventSourcingHandler
    public void on(OrderFailedEvent event) {
        this.status = FAILED;
    }
}
