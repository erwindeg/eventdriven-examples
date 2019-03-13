package nl.trifork.coins.trading.command;


import nl.trifork.coins.coreapi.FailOrderCommand;
import nl.trifork.coins.coreapi.MutateLedgerCommand;
import nl.trifork.coins.coreapi.OrderExecutedEvent;
import nl.trifork.model.CoinType;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderSagaTest {

    @Mock
    private CommandGateway commandGateway;

    @InjectMocks
    private OrderSaga orderSaga;

    @Test
    public void shouldSendMutateLedgerCommandForExecutedOrder() {
        when(commandGateway.send(any(MutateLedgerCommand.class))).thenReturn(CompletableFuture.completedFuture("userId"));
        orderSaga.on(new OrderExecutedEvent("quoteId_order", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN));
        verify(commandGateway).send(any(MutateLedgerCommand.class));
    }


    @Test
    public void shouldSendOrderFailedCommandForFailedMutation() {
        when(commandGateway.send(any(MutateLedgerCommand.class))).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException();
        }));
        orderSaga.on(new OrderExecutedEvent("quoteId_order", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN));
        verify(commandGateway).send(any(FailOrderCommand.class));
    }
}
