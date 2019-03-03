package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.CoinDto;
import nl.trifork.coins.coreapi.GetCoinQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MarketRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketRestController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public MarketRestController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(value = "/market",
            produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.GET)
    public Mono<CoinDto> getMarket() {
        //TODO: Error handling
        SubscriptionQueryResult<CoinDto, CoinDto> query = this.queryGateway.subscriptionQuery(new GetCoinQuery("1"), CoinDto.class, CoinDto.class);

        Mono<CoinDto> initial = query.initialResult();
        Flux<CoinDto> updates = query.updates();

        return initial.doOnEach(i -> LOGGER.info("Initial {}", i)).flatMap(v -> updates.next()).doOnEach(u -> LOGGER.info("Update {}", u));
    }
}
