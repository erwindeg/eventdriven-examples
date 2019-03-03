package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.CreateOrderCommand;
import nl.trifork.coins.coreapi.QuoteGeneratedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private CommandGateway commandGateway;

    @EventHandler
    public void createOrder(QuoteGeneratedEvent event) {
        LOGGER.info("Generating Order for quote");
        this.commandGateway.send(new CreateOrderCommand(event.getId()+"_order", event.getUserId(), event.getFromCurrency(), event.getToCurrency(), event.getAmount(), event.getPrice()));
    }
}
