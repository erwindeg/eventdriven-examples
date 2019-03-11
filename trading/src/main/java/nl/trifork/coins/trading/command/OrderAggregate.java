package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.*;
import nl.trifork.model.CoinType;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

import static nl.trifork.coins.coreapi.OrderStatus.*;
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

    public OrderAggregate(){}

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        apply(new OrderCreatedEvent(command.getId(), command.getUserId(), command.getFromCurrency(), command.getToCurrency(), command.getAmount(), command.getPrice()));
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


    //TODO: validate state, this command should fail if order is not pending
    @CommandHandler
    public void executeOrder(ExecuteOrderCommand command) {
        if (!command.getUserId().equals(this.userId)) {
            throw new IllegalArgumentException("Quote ID is valid not for this user");
        } else {
            apply(new OrderExecutedEvent(command.getId(), this.userId, this.fromCurrency, this.toCurrency, this.amount, this.price));
        }
    }

    @EventSourcingHandler
    public void on(OrderExecutedEvent event) {
        this.status = PENDING;
    }

    @CommandHandler
    public void success(SuccessOrderCommand command){
        apply(new OrderSuccessEvent(command.getId()));
    }

    @EventSourcingHandler
    public void on(OrderSuccessEvent event) {
        this.status = COMPLETED;
    }

    @CommandHandler
    public void fail(FailOrderCommand command){
        apply(new OrderFailedEvent(command.getId()));
    }

    @EventSourcingHandler
    public void on(OrderFailedEvent event) {
        this.status = FAILED;
    }
}
