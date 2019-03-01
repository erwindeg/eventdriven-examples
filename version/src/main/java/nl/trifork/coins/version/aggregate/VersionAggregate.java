package nl.trifork.coins.version.aggregate;

import nl.trifork.coins.coreapi.CreateVersionCommand;
import nl.trifork.coins.coreapi.VersionCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class VersionAggregate {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionAggregate.class);

    @AggregateIdentifier
    private String id;

    @CommandHandler
    public VersionAggregate(CreateVersionCommand command) {
        apply(new VersionCreatedEvent(command.getId()));
    }

    @EventHandler
    public void handle(VersionCreatedEvent event) {
        this.id = event.getId();
    }
}
