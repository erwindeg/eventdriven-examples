package nl.trifork.coins.market;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinsQuery;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MarketServiceTest {

    private static final String MOCK_MARKETS_URL = "http://localhost:8099";
    private static final String MOCK_RESPONSE_BODY1 = "{ \"status\": \"success\", \"data\": { \"base\": { \"symbol\": \"USD\", \"sign\": \"$\" }, \"coin\": { \"id\": 2, \"slug\": \"bitcoin-btc\", \"symbol\": \"BTC\", \"name\": \"Bitcoin\", \"price\": \"3320.4729487729\" } } }";
    private static final String MOCK_RESPONSE_BODY2 = "{ \"status\": \"success\", \"data\": { \"base\": { \"symbol\": \"USD\", \"sign\": \"$\" }, \"coin\": { \"id\": 2, \"slug\": \"ethereum-eth\", \"symbol\": \"ETH\", \"name\": \"Ethereum\", \"price\": \"140.4729487729\" } } }";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8099);

    @Mock
    private QueryUpdateEmitter queryUpdateEmitter;

    @InjectMocks
    private MarketService marketService;

    @Before
    public void setup() {
        this.marketService = new MarketService(MOCK_MARKETS_URL);
        createMarketsStub();
    }

    private void createMarketsStub() {
        stubFor(get(urlEqualTo("/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(MOCK_RESPONSE_BODY1)
                        .withFixedDelay(100)
                        .withStatus(200)));
        stubFor(get(urlEqualTo("/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(MOCK_RESPONSE_BODY2)
                        .withFixedDelay(100)
                        .withStatus(200)));
    }


    //TODO: test error cases

    /*
     * This test must pass to complete Exercise 1
     */
    @Test
    public void shouldReturnSingleCoinDataOnValidResponse() {
        Mono<CoinDto> response = marketService.retrieveSingleCoinData("1");
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
        Flux<CoinDto> response = marketService.retrieveMultipleCoinsData(Arrays.asList("1", "2"));
        List<CoinDto> coins = response.collectList().block();
        assertNotNull(coins);
        assertEquals(2, coins.size());
        assertEquals("BTC", coins.get(0).getCurrency());
        assertEquals("ETH", coins.get(1).getCurrency());
    }

    @Test
    public void shouldEmitItems() {
        marketService.queryAll(new GetCoinsQuery(Arrays.asList("1", "2")));
        verify(queryUpdateEmitter, times(2)).emit(eq(GetCoinsQuery.class), any(), any(CoinDto.class));
    }
}
