package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.ExecuteOrderCommand;
import nl.trifork.coins.coreapi.GetOrderQuery;
import nl.trifork.coins.coreapi.OrderDto;
import nl.trifork.coins.coreapi.OrderRequestDto;
import nl.trifork.coins.coreapi.OrderStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
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
    public Mono<ResponseEntity<OrderDto>> executeOrder(@RequestBody OrderRequestDto orderRequest) {
        String orderId = orderRequest.getQuoteId() + "_order";
        LOGGER.info("Executing order {}", orderId);
        fromFuture(this.commandGateway.send(new ExecuteOrderCommand(orderId, orderRequest.getUserId()))).log()
                .onErrorReturn(status(NOT_FOUND).build()).subscribe();

        return this.queryGateway.subscriptionQuery(new GetOrderQuery(orderId), OrderDto.class, OrderDto.class).updates()
                .doOnEach(order -> {
                    LOGGER.info("Order {}", order);
                })
                .filter(order -> !order.getStatus().equals(OrderStatus.PENDING))
                .next()
                .timeout(ofSeconds(3))
                .map(ResponseEntity::ok)
                .onErrorReturn(status(INTERNAL_SERVER_ERROR).build());
    }
}
