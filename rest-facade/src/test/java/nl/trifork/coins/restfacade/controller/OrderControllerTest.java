package nl.trifork.coins.restfacade.controller;


import io.axoniq.axonserver.grpc.ErrorMessage;
import nl.trifork.coins.coreapi.GetOrderQuery;
import nl.trifork.coins.coreapi.OrderDto;
import nl.trifork.coins.coreapi.OrderRequestDto;
import nl.trifork.coins.coreapi.OrderStatus;
import nl.trifork.model.CoinType;
import org.axonframework.axonserver.connector.command.AxonServerRemoteCommandHandlingException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static reactor.core.publisher.Flux.just;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    @Mock
    private CommandGateway commandGateway;
    @Mock
    private QueryGateway queryGateway;

    @InjectMocks
    private OrderController orderControllerController;

    @Test
    public void shouldReturnSuccessfulOrder() {
        SubscriptionQueryResult queryResultMock = mock(SubscriptionQueryResult.class);
        when(commandGateway.send(any())).thenReturn(CompletableFuture.completedFuture("orderId"));
        when(queryGateway.subscriptionQuery(any(GetOrderQuery.class), eq(OrderDto.class), eq(OrderDto.class)))
                .thenReturn(queryResultMock);
        when(queryResultMock.updates())
                .thenReturn(just(
                        new OrderDto("orderId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN, OrderStatus.PENDING),
                        new OrderDto("orderId", CoinType.EUR, CoinType.BTC, BigDecimal.ONE, BigDecimal.TEN, OrderStatus.COMPLETED)
                ));
        ResponseEntity<OrderDto> orderResponse = this.orderControllerController.executeOrder(new OrderRequestDto("userId", "quoteId")).block();
        assertNotNull(orderResponse);
        assertEquals(orderResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(OrderStatus.COMPLETED, orderResponse.getBody().getStatus());
    }

    @Test
    public void shouldReturnNotFoundForNonExistingOrder() {
        SubscriptionQueryResult queryResultMock = mock(SubscriptionQueryResult.class);
        when(commandGateway.send(any())).thenReturn(Mono.error(new CompletionException(new AxonServerRemoteCommandHandlingException("", ErrorMessage.getDefaultInstance()))).toFuture());
        when(queryGateway.subscriptionQuery(any(GetOrderQuery.class), eq(OrderDto.class), eq(OrderDto.class)))
                .thenReturn(queryResultMock);
        when(queryResultMock.updates())
                .thenReturn(Flux.never());
        ResponseEntity<OrderDto> orderResponse = this.orderControllerController.executeOrder(new OrderRequestDto("userId", "quoteId")).block();
        assertNotNull(orderResponse);
        assertEquals(orderResponse.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturnInternalServerErrorForTimeoutException() {
        SubscriptionQueryResult queryResultMock = mock(SubscriptionQueryResult.class);
        when(commandGateway.send(any())).thenReturn(CompletableFuture.completedFuture("orderId"));
        when(queryGateway.subscriptionQuery(any(GetOrderQuery.class), eq(OrderDto.class), eq(OrderDto.class)))
                .thenReturn(queryResultMock);
        when(queryResultMock.updates())
                .thenReturn(Flux.never());
        ResponseEntity<OrderDto> orderResponse = this.orderControllerController.executeOrder(new OrderRequestDto("userId", "quoteId")).block();
        assertNotNull(orderResponse);
        assertEquals(orderResponse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
