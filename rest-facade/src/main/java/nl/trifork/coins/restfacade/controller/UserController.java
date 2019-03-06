package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.CreateLedgerCommand;
import nl.trifork.coins.coreapi.GetLedgerQuery;
import nl.trifork.coins.coreapi.LedgerDto;
import nl.trifork.coins.coreapi.UserRequestDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static reactor.core.publisher.Mono.*;

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
    public Mono<ResponseEntity> createUser(@RequestBody UserRequestDto userRequest) {
        CompletableFuture<Object> completableFuture = commandGateway.send(new CreateLedgerCommand(userRequest.getUserId()));
        return fromFuture(completableFuture)
                .map(id -> new ResponseEntity(CREATED))
                .onErrorReturn(new ResponseEntity(CONFLICT));
    }

    @GetMapping("/{userId}")
    public Mono<LedgerDto> getUser(@PathVariable String userId) {
        //TODO: error handling to get 404 when user isn't found
        return fromFuture(queryGateway.query(new GetLedgerQuery(userId), LedgerDto.class));
    }

}
