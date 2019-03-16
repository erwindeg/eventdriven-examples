package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.ExecuteOrderCommand;
import nl.trifork.coins.coreapi.GetOrderQuery;
import nl.trifork.coins.coreapi.OrderDto;
import nl.trifork.coins.coreapi.OrderRequestDto;
import nl.trifork.coins.coreapi.OrderStatus;
import org.axonframework.axonserver.connector.command.AxonServerRemoteCommandHandlingException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.status;
import static reactor.core.publisher.Mono.*;
import static reactor.core.publisher.Mono.fromFuture;
import static reactor.core.publisher.Mono.zip;

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
    public Mono<ResponseEntity<OrderDto>> executeOrder(@RequestBody OrderRequestDto orderRequest) {
        String orderId = orderRequest.getQuoteId() + "_order";
        LOGGER.info("Executing order {}", orderId);

        Mono<String> command = defer(() -> fromFuture(this.commandGateway.send(new ExecuteOrderCommand(orderId, orderRequest.getUserId()))));
        Mono<ResponseEntity<OrderDto>> query = this.queryGateway.subscriptionQuery(new GetOrderQuery(orderId), OrderDto.class, OrderDto.class)
                .updates()
                .filter(order -> !order.getStatus().equals(OrderStatus.PENDING))
                .next()
                .timeout(ofSeconds(3))
                .map(ResponseEntity::ok);

        return zip(command, query, (c, q) -> q)
                .onErrorReturn(error -> error.getCause() instanceof AxonServerRemoteCommandHandlingException, notFound().build())
                .onErrorReturn(status(INTERNAL_SERVER_ERROR).build());
    }
}
