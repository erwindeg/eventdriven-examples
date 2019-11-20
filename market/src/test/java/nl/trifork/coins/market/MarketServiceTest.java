package nl.trifork.coins.market;

import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinQuery;
import nl.trifork.coins.coreapi.GetCoinsQuery;
import nl.trifork.model.CoinType;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarketServiceTest {

    private static final String MOCK_RESPONSE_BODY1 = "{ \"status\": \"success\", \"data\": { \"base\": { \"symbol\": \"USD\", \"sign\": \"$\" }, \"coin\": { \"id\": 2, \"slug\": \"bitcoin-btc\", \"symbol\": \"BTC\", \"name\": \"Bitcoin\", \"price\": \"3320.4729487729\" } } }";
    private static final String MOCK_RESPONSE_BODY2 = "{ \"status\": \"success\", \"data\": { \"base\": { \"symbol\": \"USD\", \"sign\": \"$\" }, \"coin\": { \"id\": 2, \"slug\": \"ethereum-eth\", \"symbol\": \"ETH\", \"name\": \"Ethereum\", \"price\": \"140.4729487729\" } } }";
    private static final CoinDto COIN_BTC = new CoinDto("BTC", BigDecimal.valueOf(3320.4729487729));
    private static final CoinDto COIN_ETH = new CoinDto("ETH", BigDecimal.valueOf(140.4729487729));

    @Mock
    private CoinRankingClient coinRankingClient;

    @Mock
    private QueryUpdateEmitter queryUpdateEmitter;

    @InjectMocks
    private MarketService marketService;


    /*
     * This test must pass to complete Exercise 1
     */
    @Test
    public void shouldReturnSingleCoinDataOnValidResponse() {
        when(coinRankingClient.getCoinInformation(eq(CoinType.BTC))).thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK).header("content-type", MediaType.APPLICATION_JSON.toString()).body(MOCK_RESPONSE_BODY1).build()));
        Mono<CoinDto> response = marketService.retrieveSingleCoinData(CoinType.BTC);
        CoinDto coin = response.block();
        assertNotNull(coin);
        assertEquals("BTC", coin.getCurrency());
        assertEquals(new BigDecimal("3320.4729487729"), coin.getPrice());
    }

    /*
     * This test must pass to complete Exercise 3
     */
    @Test
    public void shouldReturnMultipleCoinsDataOnValidResponses() {
        when(coinRankingClient.getCoinInformation(eq(CoinType.BTC))).thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK).header("content-type", MediaType.APPLICATION_JSON.toString()).body(MOCK_RESPONSE_BODY1).build()));
        when(coinRankingClient.getCoinInformation(eq(CoinType.ETH))).thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK).header("content-type", MediaType.APPLICATION_JSON.toString()).body(MOCK_RESPONSE_BODY2).build()));
        Flux<CoinDto> response = marketService.retrieveMultipleCoinsData(Arrays.asList(CoinType.BTC, CoinType.ETH));
        List<CoinDto> coins = response.collectList().block();
        assertNotNull(coins);
        assertEquals(2, coins.size());
        assertThat(coins).containsExactlyInAnyOrder(COIN_BTC, COIN_ETH);
    }


    @Test
    public void shouldEmitItem() {
        when(coinRankingClient.getCoinInformation(eq(CoinType.BTC))).thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK).header("content-type", MediaType.APPLICATION_JSON.toString()).body(MOCK_RESPONSE_BODY1).build()));
        marketService.query(new GetCoinQuery(CoinType.BTC));
        verify(queryUpdateEmitter, timeout(1000).times(1)).emit(eq(GetCoinQuery.class), any(), any(CoinDto.class));
    }

    @Test
    public void shouldEmitExceptionForQuery() {
        when(coinRankingClient.getCoinInformation(any())).thenReturn(Mono.error(new RuntimeException()));
        marketService.query(new GetCoinQuery(CoinType.BTC));
        verify(queryUpdateEmitter, timeout(1000).times(1)).completeExceptionally(eq(GetCoinQuery.class), any(), any(RuntimeException.class));
    }

    @Test
    public void shouldEmitItems() {
        when(coinRankingClient.getCoinInformation(eq(CoinType.BTC))).thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK).header("content-type", MediaType.APPLICATION_JSON.toString()).body(MOCK_RESPONSE_BODY1).build()));
        when(coinRankingClient.getCoinInformation(eq(CoinType.ETH))).thenReturn(Mono.just(ClientResponse.create(HttpStatus.OK).header("content-type", MediaType.APPLICATION_JSON.toString()).body(MOCK_RESPONSE_BODY2).build()));
        marketService.queryAll(new GetCoinsQuery(Arrays.asList(CoinType.BTC, CoinType.ETH)));
        verify(queryUpdateEmitter, timeout(1000).times(2)).emit(eq(GetCoinsQuery.class), any(), any(CoinDto.class));
        verify(queryUpdateEmitter, timeout(1000).times(1)).complete(eq(GetCoinsQuery.class), any());
    }

    @Test
    public void shouldEmitExceptionForQueryAll() {
        when(coinRankingClient.getCoinInformation(any())).thenReturn(Mono.error(new RuntimeException()));
        marketService.queryAll(new GetCoinsQuery(Arrays.asList(CoinType.BTC, CoinType.ETH)));
        verify(queryUpdateEmitter, timeout(1000).times(1)).completeExceptionally(eq(GetCoinsQuery.class), any(), any(RuntimeException.class));
    }
}
