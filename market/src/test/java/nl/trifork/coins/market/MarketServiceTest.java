package nl.trifork.coins.market;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import nl.trifork.coins.coreapi.CoinDto;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class MarketServiceTest {

    private static final String MOCK_MARKETS_URL = "localhost";
    private static final String MOCK_RESPONSE_BODY = "{ \"status\": \"success\", \"data\": { \"base\": { \"symbol\": \"USD\", \"sign\": \"$\" }, \"coin\": { \"id\": 2, \"slug\": \"ethereum-eth\", \"symbol\": \"ETH\", \"name\": \"Ethereum\", \"price\": \"140.4729487729\" } } }";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8099);
    private MarketService marketService;

    @Before
    public void setup() {
        this.marketService = new MarketService("http", MOCK_MARKETS_URL, 8099);
        createMarketsStub();
    }

    private void createMarketsStub() {
        stubFor(get(urlEqualTo("/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(MOCK_RESPONSE_BODY)
                        .withFixedDelay(100)
                        .withStatus(200)));
    }


    @Test
    public void shouldReturnMarketDataOnValidResponse() {
        Mono<CoinDto> response = marketService.retrieveMarketData("1");
        CoinDto coin = response.block();
        assertNotNull(coin);
        assertEquals("ETH", coin.getCurrency());
        assertEquals(new BigDecimal("140.4729487729"), coin.getPrice());
    }

}
