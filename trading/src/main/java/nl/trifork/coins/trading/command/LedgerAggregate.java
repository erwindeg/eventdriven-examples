package nl.trifork.coins.trading.command;

import nl.trifork.coins.coreapi.CreateLedgerCommand;
import nl.trifork.coins.coreapi.LedgerCreatedEvent;
import nl.trifork.coins.coreapi.LedgerMutatedEvent;
import nl.trifork.coins.coreapi.MutateLedgerCommand;
import nl.trifork.model.CoinType;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class LedgerAggregate {

    private static final Logger LOGGER = LoggerFactory.getLogger(LedgerAggregate.class);

    @AggregateIdentifier
    private String userId;
    private Map<CoinType, BigDecimal> assets;

    public LedgerAggregate() {
    }

    @CommandHandler
    public LedgerAggregate(CreateLedgerCommand command) {
        LOGGER.info("Creating ledger for user {}", command.getUserId());
        Map<CoinType, BigDecimal> newAssets = new HashMap<>();
        newAssets.put(CoinType.EUR, new BigDecimal("10000"));
        apply(new LedgerCreatedEvent(command.getUserId(), newAssets));
    }

    @EventSourcingHandler
    public void on(LedgerCreatedEvent event) {
        this.userId = event.getUserId();
        this.assets = event.getAssets();
    }

    @CommandHandler
    public void executeMutation(MutateLedgerCommand command) {
        BigDecimal fromCurrencyAmount = assets.get(command.getFromCurrency());
        if (fromCurrencyAmount == null || fromCurrencyAmount.compareTo(command.getFromAmount()) < 1) {
            throw new IllegalStateException("Not enough credit " + command.getFromCurrency());
        }

        BigDecimal toCurrencyAmount = assets.get(command.getToCurrency()) != null ? assets.get(command.getToCurrency()) : BigDecimal.ZERO;
        Map<CoinType, BigDecimal> mutatedAssets = new HashMap<>(assets);
        mutatedAssets.put(command.getFromCurrency(), fromCurrencyAmount.subtract(command.getFromAmount()));
        mutatedAssets.put(command.getToCurrency(), toCurrencyAmount.add(command.getToAmount()));
        apply(new LedgerMutatedEvent(this.userId, mutatedAssets));
    }

    @EventSourcingHandler
    public void on(LedgerMutatedEvent event) {
        this.assets = event.getAssets();
    }
}
