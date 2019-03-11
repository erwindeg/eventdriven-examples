package nl.trifork.coins.market;

import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinQuery;
import nl.trifork.coins.coreapi.GetCoinsQuery;
import nl.trifork.model.CoinType;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketService.class);

    private final QueryUpdateEmitter queryUpdateEmitter;
    private final CoinrankingClient coinrankingClient;

    public MarketService(QueryUpdateEmitter queryUpdateEmitter, CoinrankingClient coinrankingClient) {
        this.queryUpdateEmitter = queryUpdateEmitter;
        this.coinrankingClient = coinrankingClient;
    }

    @QueryHandler
    public CoinDto query(GetCoinQuery getCoinQuery) {
        LOGGER.info("GetCoinQuery {}", getCoinQuery.getCoinType().name());
        retrieveSingleCoinData(getCoinQuery.getCoinType()).subscribe(
                coin -> this.queryUpdateEmitter.emit(GetCoinQuery.class, query -> true, coin),
                error -> this.queryUpdateEmitter.completeExceptionally(GetCoinQuery.class, query -> getCoinQuery.getCoinType().equals(query.getCoinType()), error));
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


    public Mono<CoinDto> retrieveSingleCoinData(CoinType coinType) {
        return toCoinDtoMono(coinrankingClient.getCoinInformation(coinType));
    }

    public Mono<CoinDto> retrieveSingleCoinDataWithBaseCurrency(CoinType fromCurrency, CoinType toCurrency) {
        return toCoinDtoMono(coinrankingClient.getCoinInformationWithBaseCurrency(fromCurrency, toCurrency));
    }

    //Exercise 1: implement this method
    private Mono<CoinDto> toCoinDtoMono(Mono<ClientResponse> coinInfo) {
        return coinInfo.flatMap(response -> response.bodyToMono(HashMap.class)
                .map(result -> (Map) result.get("data"))
                .map(data -> (Map) data.get("coin"))
                .map(coin -> new CoinDto((String) coin.get("symbol"), new BigDecimal((String) coin.get("price")))));
    }

    //Exercise 3: implement
    public Flux<CoinDto> retrieveMultipleCoinsData(List<CoinType> coinTypes) {
        return Flux.fromIterable(coinTypes).flatMap(this::retrieveSingleCoinData);
    }
}
