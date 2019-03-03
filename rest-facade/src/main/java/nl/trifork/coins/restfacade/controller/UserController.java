package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.CreateLedgerCommand;
import nl.trifork.coins.coreapi.UserRequestDto;
import nl.trifork.coins.coreapi.GetLedgerQuery;
import nl.trifork.coins.coreapi.LedgerDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.fromFuture;

@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public UserController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(value = "/user",
            produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.POST)
    public Mono<ResponseEntity> createUser(@RequestBody UserRequestDto userRequest) {
        return fromFuture(this.commandGateway.send(new CreateLedgerCommand(userRequest.getUserId())))
                .map(id -> new ResponseEntity(HttpStatus.CREATED))
                .onErrorReturn(new ResponseEntity(HttpStatus.CONFLICT));
    }

    @RequestMapping(value = "/user",
            produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.GET)
    public Mono<LedgerDto> getUser(@RequestBody UserRequestDto userRequest) {
        return fromFuture(this.queryGateway.query(new GetLedgerQuery(userRequest.getUserId()), LedgerDto.class));
    }
}
