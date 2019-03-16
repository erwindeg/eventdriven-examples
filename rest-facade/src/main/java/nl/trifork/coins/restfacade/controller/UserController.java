package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.CreateLedgerCommand;
import nl.trifork.coins.coreapi.GetLedgerQuery;
import nl.trifork.coins.coreapi.UserRequestDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.status;
import static reactor.core.publisher.Mono.fromFuture;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public UserController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public Mono<ResponseEntity> createUser(@RequestBody UserRequestDto userRequest, UriComponentsBuilder uriComponentsBuilder) {
        CompletableFuture<Object> completableFuture = commandGateway.send(new CreateLedgerCommand(userRequest.getUserId()));

        return fromFuture(completableFuture)
                .map(id -> created(uriComponentsBuilder.path("/user/{id}").buildAndExpand(id).toUri()))
                .onErrorReturn(status(CONFLICT))
                .map(ResponseEntity.HeadersBuilder::build);
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity> getUser(@PathVariable String userId) {
        CompletableFuture<Optional> completableFuture = queryGateway.query(new GetLedgerQuery(userId), Optional.class);
        return fromFuture(completableFuture)
                .map(ResponseEntity::of);
    }

}
