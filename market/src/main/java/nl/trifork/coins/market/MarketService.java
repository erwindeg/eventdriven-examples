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

import java.util.List;

@Service
public class MarketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketService.class);

    private final QueryUpdateEmitter queryUpdateEmitter;
    private final CoinRankingClient coinRankingClient;

    public MarketService(QueryUpdateEmitter queryUpdateEmitter, CoinRankingClient coinRankingClient) {
        this.queryUpdateEmitter = queryUpdateEmitter;
        this.coinRankingClient = coinRankingClient;
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
    //FIXME Exercise 4: Subscribe to the Flux and emit each item using the queryUpdateEmitter. Don't forget to handle the exceptions. Additionally we should signal when we
    //are done emitting items.
    public CoinDto queryAll(GetCoinsQuery getCoinsQuery) {
        LOGGER.info("GetCoinsQuery {}", getCoinsQuery.getIds());
        retrieveMultipleCoinsData(getCoinsQuery.getIds())
                .doOnComplete(() -> this.queryUpdateEmitter.complete(GetCoinsQuery.class, query -> getCoinsQuery.getIds().equals(query.getIds())))
                .subscribe(
                        coin -> this.queryUpdateEmitter.emit(GetCoinsQuery.class, query -> getCoinsQuery.getIds().equals(query.getIds()), coin),
                        error -> this.queryUpdateEmitter.completeExceptionally(GetCoinsQuery.class, query -> true, error)
                );
        return new CoinDto(null, null);
    }

    //FIXME Exercise 1: uncomment the toCoinDtoMono call and implement it
    //Hints: You need to map the response
    public Mono<CoinDto> retrieveSingleCoinData(CoinType coinType) {
        return Mono.empty();
//        return toCoinDtoMono(coinRankingClient.getCoinInformation(coinType));
    }

    public Mono<CoinDto> retrieveSingleCoinDataWithBaseCurrency(CoinType fromCurrency, CoinType toCurrency) {
        return toCoinDtoMono(coinRankingClient.getCoinInformationWithBaseCurrency(fromCurrency, toCurrency));
    }

    //FIXME Exercise 3: we can call the retrieveSingleCoinData multiple times to return a Flux
    //Hints: Implement this method
    public Flux<CoinDto> retrieveMultipleCoinsData(List<CoinType> coinTypes) {
        return Flux.empty();
    }

    private Mono<CoinDto> toCoinDtoMono(Mono<ClientResponse> coinInfo) {
        return Mono.empty();
    }
}
