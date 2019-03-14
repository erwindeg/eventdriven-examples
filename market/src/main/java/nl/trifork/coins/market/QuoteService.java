package nl.trifork.coins.market;

import nl.trifork.coins.coreapi.GenerateQuoteCommand;
import nl.trifork.coins.coreapi.GenerateQuoteFailedEvent;
import nl.trifork.coins.coreapi.GetQuoteQuery;
import nl.trifork.coins.coreapi.GetQuoteResponse;
import nl.trifork.coins.coreapi.QuoteDto;
import nl.trifork.coins.coreapi.QuoteGeneratedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteService.class);

    @Autowired
    MarketService marketService;

    @Autowired
    EventBus eventBus;

    @Autowired
    QueryUpdateEmitter queryUpdateEmitter;

    @CommandHandler
    //FIXME Exercise 6: subcribe to the result of retrieveSingleCoinDataWithBaseCurrency and the send a QuoteGeneratedEvent on success and a GenerateQuoteFailedEvent on error.
    public void generateQuote(GenerateQuoteCommand command) {
        LOGGER.info("GenerateQuoteCommand {}", command);
        this.marketService.retrieveSingleCoinDataWithBaseCurrency(command.getFromCurrency(), command.getToCurrency());
    }

    @EventHandler
    public void on(QuoteGeneratedEvent event) {
        LOGGER.info("QuoteGeneratedEvent {}", event);
        queryUpdateEmitter.emit(GetQuoteQuery.class, query -> query.getId().equals(event.getId()), new GetQuoteResponse(new QuoteDto(event.getId(), event.getFromCurrency(), event.getToCurrency(), event.getAmount(), event.getPrice())));
    }

    @EventHandler
    public void on(GenerateQuoteFailedEvent event) {
        LOGGER.info("GenerateQuoteFailedEvent {}", event);
        queryUpdateEmitter.completeExceptionally(GetQuoteQuery.class, query -> query.getId().equals(event.getId()), event.getCause());
    }

    @QueryHandler
    public GetQuoteResponse getQuote(GetQuoteQuery query) {
        //normally implemented by looking up the quote in the read model
        return new GetQuoteResponse(null);
    }
}
