package nl.trifork.coins.restfacade.controller;


import nl.trifork.coins.coreapi.CreateVersionCommand;
import nl.trifork.coins.coreapi.FindVersionQuery;
import nl.trifork.coins.coreapi.VersionDto;
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

import java.util.UUID;

@RestController
public class VersionRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionRestController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public VersionRestController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(value = "/version",
            produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.GET)
    public Mono<String> getVersion() {
        String id = UUID.randomUUID().toString();
        SubscriptionQueryResult<VersionDto, VersionDto> query = this.queryGateway.subscriptionQuery(new FindVersionQuery(id), VersionDto.class, VersionDto.class);
        this.commandGateway.send(new CreateVersionCommand(id));

        Mono<VersionDto> initial = query.initialResult();
        Flux<VersionDto> updates = query.updates();

        return initial.doOnEach(i -> LOGGER.info("Initial {}", i)).flatMap(v -> updates.next()).doOnEach(u -> LOGGER.info("Update {}", u)).map(u -> u.getId());
    }
}
