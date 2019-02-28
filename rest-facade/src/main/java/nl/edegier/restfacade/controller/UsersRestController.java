package nl.edegier.restfacade.controller;


import nl.edegier.coreapi.FindUsersQuery;
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

import java.util.List;

@RestController
public class UsersRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersRestController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public UsersRestController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(value = "/users",
            produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.GET)
    public Mono<List> getUsers() {
        SubscriptionQueryResult<List, List> query = this.queryGateway.subscriptionQuery(new FindUsersQuery(), List.class, List.class);

        Mono<List> initial = query.initialResult();
        Flux<List> updates = query.updates();

        return initial.doOnEach(i -> LOGGER.info("Initial {}", i)).flatMap(v -> updates.next()).doOnEach(u -> LOGGER.info("Update {}", u));
    }
}
