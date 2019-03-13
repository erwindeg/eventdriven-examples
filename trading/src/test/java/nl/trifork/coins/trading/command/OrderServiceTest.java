package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.CreateOrderCommand;
import nl.trifork.coins.coreapi.QuoteGeneratedEvent;
import nl.trifork.model.CoinType;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    private CommandGateway commandGateway;

    @InjectMocks
    private OrderService orderService;


    @Test
    //Test must pass to complete exercise 7
    public void shouldSendCreateOrderCommandForQuoteGeneratedEvent() {
        this.orderService.createOrder(new QuoteGeneratedEvent("quoteId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN));
        verify(commandGateway).send(eq(new CreateOrderCommand("quoteId_order","userId", CoinType.EUR,CoinType.BTC, BigDecimal.ONE,BigDecimal.TEN)));
    }

}
