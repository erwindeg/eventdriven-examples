package nl.trifork.coins.market;

import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.client.WebClient.create;

@Service
public class MarketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketService.class);

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public CoinDto query(GetCoinQuery getCoinQuery) {
        LOGGER.info("GetCoinQuery {}", getCoinQuery.getId());
        retrieveMarketData(getCoinQuery.getId()).subscribe(coin -> this.queryUpdateEmitter.emit(GetCoinQuery.class, query -> true, coin));
        return new CoinDto("", BigDecimal.ZERO);
    }

    public Mono<CoinDto> retrieveMarketData(String coinId) {
        return create("https://api.coinranking.com/v1/public/coin/1").get()
                .exchange()
                .flatMap(response -> response.bodyToMono(HashMap.class)
                        .map(result -> (Map) result.get("data"))
                        .map(data -> (Map) data.get("coin"))
                        .map(coin -> new CoinDto((String) coin.get("symbol"), new BigDecimal((String) coin.get("price")))));
    }
}
