package nl.trifork.coins.restfacade.controller;


import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinQuery;
import nl.trifork.coins.coreapi.GetCoinsQuery;
import nl.trifork.model.CoinType;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/market")
public class MarketController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public MarketController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    /*
     * This market controller will use the SubscriptionQuery from Axon Framework to retrieve market information for cryptocurrency
     * The Subscription query has two methods for retrieving the data, initialResult and updates.
     * initialResult gives back the value returned from the QueryHandler method, this a synchronous return.
     * To return data async, the QueryUpdateEmitter must be used. The data emitted can be retrieved by subscripting to the
     * Flux which is returned from the SubscriptionQueryResult.updates() method.
     * Since Spring Webflux will actually subscribe for us, we can just return the desired value from this method
     * Since we return 1 response to the client, we use a Mono.
     *
     */
    @GetMapping
    // Exercise 5
    public Mono<ResponseEntity<List<CoinDto>>> getMarket() {
        SubscriptionQueryResult<CoinDto, CoinDto> query = this.queryGateway.subscriptionQuery(new GetCoinsQuery(Arrays.asList(CoinType.BTC, CoinType.ETH)), CoinDto.class, CoinDto.class);

        //We have to call initialResult() to fire the QueryHandler in MarketService. We subscribe to the updates() to receive
        //the data which is emitted through the QueryUpdateEmitter in MarketService
        query.initialResult().subscribe();
        Flux<CoinDto> updates = query.updates();

        //retrieve all elements emitted by using collectList(), this turns the Flux into a Mono with the list containing all elements
        //specify a timeout of 3 seconds, to  make sure this method doesn't wait forever when no data is emitted
        //when the timeout expires, an exception is thrown.
        //use a map to return a ResponseEntity, use onErrorReturn to return 404 not found in case of an error.
        return updates
                .collectList()
                .map(ResponseEntity::ok)
                .timeout(ofSeconds(3))
                .doOnError(error -> LOGGER.error("{}", error))
                .onErrorReturn(new ResponseEntity(NOT_FOUND));
    }

    @GetMapping("/{coinId}")
    //Exercise 2: map the response from the "updates" Flux to the correct response of this method
    public Mono<ResponseEntity<CoinDto>> getCoin(@PathVariable CoinType coinId) {

        SubscriptionQueryResult<CoinDto, CoinDto> query = this.queryGateway.subscriptionQuery(new GetCoinQuery(coinId), CoinDto.class, CoinDto.class);

        //We have to call initialResult() to fire the QueryHandler in MarketService. We subscribe to the updates() to receive
        //the data which is emitted through the QueryUpdateEmitter in MarketService
        query.initialResult().subscribe();
        Flux<CoinDto> updates = query.updates();

        return updates.next()
                .timeout(ofSeconds(3))
                .map(ResponseEntity::ok)
                .doOnError(error -> LOGGER.error("{}", error))
                .onErrorReturn(new ResponseEntity(NOT_FOUND));

    }
}
