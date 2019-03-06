package nl.trifork.coins.restfacade.controller;

import nl.trifork.coins.coreapi.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
    public Mono<QuoteDto> generateQuote(@RequestBody QuoteRequestDto quoteRequestDto) {
        //TODO: error handling
        String id = UUID.randomUUID().toString();
        Flux<GetQuoteResponse> quoteResponseFlux = this.queryGateway.subscriptionQuery(new GetQuoteQuery(id), GetQuoteResponse.class, GetQuoteResponse.class).updates();

        this.commandGateway.send(new GenerateQuoteCommand(id, quoteRequestDto.getUserId(), quoteRequestDto.getFromCurrency(), quoteRequestDto.getToCurrency(), quoteRequestDto.getAmount()));

        return quoteResponseFlux.next().map(quoteReponse -> quoteReponse.getQuote());
    }
}
