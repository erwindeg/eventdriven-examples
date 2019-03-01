package nl.trifork.coins.market;

import nl.trifork.coins.coreapi.GetCoinQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class MarketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketService.class);

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public Map query(GetCoinQuery getCoinQuery) {
        LOGGER.info("GetCoinQuery {}", getCoinQuery.getId());
        WebClient.create("https://api.coinranking.com/v1/public/coin/1").get()
                .exchange()
                .flatMap(response -> response.bodyToMono(HashMap.class)
                        .map(result -> (Map) result.get("data"))
                        .map(data -> data.get("coin")))
                .doOnNext(coin -> LOGGER.info("response {}", coin))
                .subscribe(coin -> this.queryUpdateEmitter.emit(GetCoinQuery.class, query -> true, coin));
        return new HashMap();
    }
}
