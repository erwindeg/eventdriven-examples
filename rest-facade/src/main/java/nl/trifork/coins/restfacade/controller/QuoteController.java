package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.GenerateQuoteCommand;
import nl.trifork.coins.coreapi.GetQuoteQuery;
import nl.trifork.coins.coreapi.GetQuoteResponse;
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

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

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
    public Mono<ResponseEntity> generateQuote(@RequestBody QuoteRequestDto quoteRequestDto) {
        String id = UUID.randomUUID().toString();
        Flux<GetQuoteResponse> quoteResponseFlux = this.queryGateway.subscriptionQuery(new GetQuoteQuery(id), GetQuoteResponse.class, GetQuoteResponse.class).updates();

        this.commandGateway.send(new GenerateQuoteCommand(id, quoteRequestDto.getUserId(), quoteRequestDto.getFromCurrency(), quoteRequestDto.getToCurrency(), quoteRequestDto.getAmount()));

        return quoteResponseFlux.next()
                .map(getQuoteResponse -> null == getQuoteResponse.getQuote() ?
                        status(INTERNAL_SERVER_ERROR).contentType(TEXT_PLAIN).body(getQuoteResponse.getError()) :
                        ok(getQuoteResponse.getQuote())
                );
    }
}
