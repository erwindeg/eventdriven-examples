package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.FailOrderCommand;
import nl.trifork.coins.coreapi.LedgerMutatedEvent;
import nl.trifork.coins.coreapi.MutateLedgerCommand;
import nl.trifork.coins.coreapi.OrderExecutedEvent;
import nl.trifork.coins.coreapi.SuccessOrderCommand;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static reactor.core.publisher.Mono.fromFuture;

@Saga
public class OrderSaga extends AbstractSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);
    private String orderId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderExecutedEvent event) {
        this.orderId = event.getOrderId();
        LOGGER.info("Mutating ledger for order {}", event.getOrderId());
        fromFuture(this.commandGateway.send(new MutateLedgerCommand(event.getUserId(), event.getOrderId(), event.getFromCurrency(), event.getPrice(), event.getToCurrency(), event.getAmount())))
                .doOnError(error -> this.commandGateway.send(new FailOrderCommand(this.orderId))).subscribe();
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(LedgerMutatedEvent event) {
        LOGGER.info("Order successful");
        this.commandGateway.send(new SuccessOrderCommand(this.orderId));
    }
}
