package nl.trifork.coins.market;

import nl.trifork.model.CoinType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.WebClient.create;

@Component
public class CoinrankingClient {

    private String baseUrl = "https://api.coinranking.com/v1/public/coin/";

    public CoinrankingClient() {
    }

    public CoinrankingClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Mono<ClientResponse> getCoinInformation(CoinType coinType) {
        return create().get().uri(UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(coinType.getCoinId())
                .build()
                .toUri())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    public Mono<ClientResponse> getCoinInformationWithBaseCurrency(CoinType fromCurrency, CoinType toCurrency) {
        return create().get().uri(UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(toCurrency.getCoinId())
                .queryParam("base", fromCurrency.name())
                .build()
                .toUri())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }
}
