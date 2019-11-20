package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.GenerateQuoteCommand;
import nl.trifork.coins.coreapi.GetQuoteQuery;
import nl.trifork.coins.coreapi.GetQuoteResponse;
import nl.trifork.coins.coreapi.QuoteDto;
import nl.trifork.coins.coreapi.QuoteRequestDto;
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

import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.status;
import static reactor.core.publisher.Mono.fromFuture;

@RestController
@RequestMapping("/quote")
public class QuoteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteController.class);

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public QuoteController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public Mono<ResponseEntity<QuoteDto>> generateQuote(@RequestBody QuoteRequestDto quoteRequestDto) {
        String id = UUID.randomUUID().toString();
        Flux<GetQuoteResponse> quoteResponseFlux = this.queryGateway.subscriptionQuery(new GetQuoteQuery(id), GetQuoteResponse.class, GetQuoteResponse.class).updates();

        return fromFuture(this.commandGateway.send(
                new GenerateQuoteCommand(id,
                        quoteRequestDto.getUserId(),
                        quoteRequestDto.getFromCurrency(),
                        quoteRequestDto.getToCurrency(),
                        quoteRequestDto.getAmount())))
                .then(quoteResponseFlux.next())
                .map(quoteResponse -> quoteResponse.getQuote())
                .map(ResponseEntity::ok)
                .timeout(ofSeconds(3))
                .onErrorReturn(status(INTERNAL_SERVER_ERROR).build());
    }
}
