package nl.trifork.coins.market;

import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinQuery;
import nl.trifork.coins.coreapi.GetCoinsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.client.WebClient.create;

@Service
public class MarketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketService.class);

    @Autowired
    QueryUpdateEmitter queryUpdateEmitter;
    String baseUrl = "https://api.coinranking.com/v1/public/coin/";

    public MarketService() {
    }

    public MarketService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @QueryHandler
    public CoinDto query(GetCoinQuery getCoinQuery) {
        LOGGER.info("GetCoinQuery {}", getCoinQuery.getId());
        retrieveSingleCoinData(getCoinQuery.getId()).subscribe(
                coin -> this.queryUpdateEmitter.emit(GetCoinQuery.class, query -> true, coin),
                error -> this.queryUpdateEmitter.completeExceptionally(GetCoinQuery.class, query -> getCoinQuery.getId().equals(query.getId()), error));
        return new CoinDto(null, null);
    }

    @QueryHandler
    //Exercise 4: add the doOnComplete
    public CoinDto queryAll(GetCoinsQuery getCoinsQuery) {
        LOGGER.info("GetCoinsQuery {}", getCoinsQuery.getIds());
        retrieveMultipleCoinsData(getCoinsQuery.getIds())
                .doOnComplete(() -> this.queryUpdateEmitter.complete(GetCoinsQuery.class, query -> true))
                .subscribe(
                        coin -> this.queryUpdateEmitter.emit(GetCoinsQuery.class, query -> getCoinsQuery.getIds().equals(query.getIds()), coin),
                        error -> this.queryUpdateEmitter.completeExceptionally(GetCoinsQuery.class, query -> true, error)
                );
        return new CoinDto(null, null);
    }


    //Exercise 1: implement this method
    public Mono<CoinDto> retrieveSingleCoinData(String coinId) {
        return callExternalService(coinId)
                .flatMap(response -> response.bodyToMono(HashMap.class)
                        .map(result -> (Map) result.get("data"))
                        .map(data -> (Map) data.get("coin"))
                        .map(coin -> new CoinDto((String) coin.get("symbol"), new BigDecimal((String) coin.get("price")))));
    }

    //Exercise 3: implement
    public Flux<CoinDto> retrieveMultipleCoinsData(List<String> coinIds) {
        return Flux.fromIterable(coinIds)
                .flatMap(coinId -> retrieveSingleCoinData(coinId));
    }

    private Mono<ClientResponse> callExternalService(String coinId) {
        return create().get().uri(UriComponentsBuilder.fromHttpUrl(this.baseUrl).path(coinId).build().toUri())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

}
