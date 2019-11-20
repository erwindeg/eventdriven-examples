package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.CreateOrderCommand;
import nl.trifork.coins.coreapi.ExecuteOrderCommand;
import nl.trifork.coins.coreapi.FailOrderCommand;
import nl.trifork.coins.coreapi.OrderCreatedEvent;
import nl.trifork.coins.coreapi.OrderExecutedEvent;
import nl.trifork.coins.coreapi.OrderFailedEvent;
import nl.trifork.coins.coreapi.OrderSuccessEvent;
import nl.trifork.coins.coreapi.SuccessOrderCommand;
import nl.trifork.model.CoinType;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

//These tests should succeed to finish the OrderAggregate exercises
public class OrderAggregateTest {

    private AggregateTestFixture<OrderAggregate> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    public void createOrder() {
        fixture.givenNoPriorActivity()
                .when(new CreateOrderCommand("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN))
                .expectEvents(new OrderCreatedEvent("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN));
    }

    @Test
    public void executeOrder() {
        fixture.givenCommands(new CreateOrderCommand("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN))
                .when(new ExecuteOrderCommand("orderId", "userId"))
                .expectEvents(new OrderExecutedEvent("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN));
    }

    @Test
    public void excutingAlreadyExecutedOrderShouldFail() {
        fixture.givenCommands(new CreateOrderCommand("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN),
                new ExecuteOrderCommand("orderId", "userId"))
                .when(new ExecuteOrderCommand("orderId", "userId"))
                .expectException(IllegalStateException.class);
    }

    @Test
    public void successOrder() {
        fixture.givenCommands(new CreateOrderCommand("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN),
                new ExecuteOrderCommand("orderId", "userId"))
                .when(new SuccessOrderCommand("orderId"))
                .expectEvents(new OrderSuccessEvent("orderId"));
    }

    @Test
    public void succesAlreadyFailedOrderShouldFail() {
        fixture.givenCommands(new CreateOrderCommand("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN),
                new ExecuteOrderCommand("orderId", "userId"),
                new FailOrderCommand("orderId"))
                .when(new SuccessOrderCommand("orderId"))
                .expectException(IllegalStateException.class);
    }

    @Test
    public void failingAlreadySuccessfulOrderShouldFail() {
        fixture.givenCommands(new CreateOrderCommand("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN),
                new ExecuteOrderCommand("orderId", "userId"),
                new SuccessOrderCommand("orderId"))
                .when(new FailOrderCommand("orderId"))
                .expectException(IllegalStateException.class);
    }

    @Test
    public void failOrder() {
        fixture.givenCommands(new CreateOrderCommand("orderId", "userId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN),
                new ExecuteOrderCommand("orderId", "userId"))
                .when(new FailOrderCommand("orderId"))
                .expectEvents(new OrderFailedEvent("orderId"));
    }
}
