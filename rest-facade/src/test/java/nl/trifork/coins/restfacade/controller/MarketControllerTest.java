package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinQuery;
import nl.trifork.model.CoinType;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarketControllerTest {

    @Mock
    private QueryGateway queryGateway;

    @InjectMocks
    private MarketController marketController;

    @Test
    public void getMarketsShouldReturnValidCoin() {
        SubscriptionQueryResult queryResultMock = mock(SubscriptionQueryResult.class);
        when(this.queryGateway.subscriptionQuery(eq(new GetCoinQuery(CoinType.BTC)), eq(CoinDto.class), eq(CoinDto.class)))
                .thenReturn(queryResultMock);
        when(queryResultMock.initialResult())
                .thenReturn(Mono.just(new CoinDto("", BigDecimal.ZERO)));
        when(queryResultMock.updates())
                .thenReturn(Flux.just(new CoinDto("BTC", new BigDecimal("3333"))));

        ResponseEntity<CoinDto> response = this.marketController.getCoin(CoinType.BTC).block();
        assertEquals("BTC", response.getBody().getCurrency());
        assertEquals(new BigDecimal("3333"), response.getBody().getPrice());
    }

    @Test
    public void getMarketsShouldReturn404ForNoData() {
        SubscriptionQueryResult queryResultMock = mock(SubscriptionQueryResult.class);
        when(this.queryGateway.subscriptionQuery(eq(new GetCoinQuery(CoinType.BTC)), eq(CoinDto.class), eq(CoinDto.class)))
                .thenReturn(queryResultMock);
        when(queryResultMock.initialResult())
                .thenReturn(Mono.just(new CoinDto("", BigDecimal.ZERO)));
        when(queryResultMock.updates())
                .thenReturn(Flux.never());

        ResponseEntity<CoinDto> response = this.marketController.getCoin(CoinType.BTC).block();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
