package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.LedgerMutatedEvent;
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

@Saga
public class OrderSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private CommandGateway commandGateway;
    private String orderId;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    //FIXME exercise 11: When an order is executed, we should try to mutate the ledger, implement the success and failure scenario's
    public void on(OrderExecutedEvent event) {
        this.orderId = event.getId();

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    public void on(LedgerMutatedEvent event) {
        this.commandGateway.send(new SuccessOrderCommand(this.orderId));
    }
}
