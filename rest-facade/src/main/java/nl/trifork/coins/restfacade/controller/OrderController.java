package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.ExecuteOrderCommand;
import nl.trifork.coins.coreapi.GetOrderQuery;
import nl.trifork.coins.coreapi.OrderDto;
import nl.trifork.coins.coreapi.OrderRequestDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.status;
import static reactor.core.publisher.Mono.fromFuture;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public OrderController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public Mono<ResponseEntity<OrderDto>> executeOrder(@RequestBody  OrderRequestDto orderRequest) {

        return fromFuture(this.commandGateway.send(new ExecuteOrderCommand(orderRequest.getQuoteId()+"_order",orderRequest.getUserId())))
                .onErrorReturn(status(NOT_FOUND).build())
                .flatMap(id -> this.queryGateway.subscriptionQuery(new GetOrderQuery(orderRequest.getQuoteId()), OrderDto.class, OrderDto.class).updates().next())
                .timeout(ofSeconds(3))
                .map(ResponseEntity::ok)
                .onErrorReturn(status(INTERNAL_SERVER_ERROR).build());
    }
}
