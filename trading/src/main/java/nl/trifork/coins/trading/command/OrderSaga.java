package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.FailOrderCommand;
import nl.trifork.coins.coreapi.LedgerMutatedEvent;
import nl.trifork.coins.coreapi.MutateLedgerCommand;
import nl.trifork.coins.coreapi.OrderExecutedEvent;
import nl.trifork.coins.coreapi.SuccessOrderCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static reactor.core.publisher.Mono.fromFuture;

@Saga
public class OrderSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private CommandGateway commandGateway;
    private String orderId;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void on(OrderExecutedEvent event) {
        this.orderId = event.getId();
        LOGGER.info("Mutating ledger for user {}", event.getUserId());
        fromFuture(this.commandGateway.send(new MutateLedgerCommand(event.getUserId(), event.getFromCurrency(), event.getPrice(), event.getToCurrency(), event.getAmount())))
                .doOnError(error -> this.commandGateway.send(new FailOrderCommand(this.orderId)));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    public void on(LedgerMutatedEvent event) {
        this.commandGateway.send(new SuccessOrderCommand(this.orderId));
    }
}
