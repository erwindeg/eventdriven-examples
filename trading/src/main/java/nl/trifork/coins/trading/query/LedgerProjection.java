package nl.trifork.coins.trading.query;

import nl.trifork.coins.coreapi.GetLedgerQuery;
import nl.trifork.coins.coreapi.LedgerCreatedEvent;
import nl.trifork.coins.coreapi.LedgerDto;
import nl.trifork.coins.coreapi.LedgerMutatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LedgerProjection {
    @Autowired
    private LedgerRepository repository;

    @EventHandler
    public void on(LedgerCreatedEvent event) {
        this.repository.save(new LedgerEntity(event.getUserId(), event.getAssets()));
    }

    @EventHandler
    public void on(LedgerMutatedEvent event) {
        LedgerEntity ledger = this.repository.getOne(event.getUserId());
        ledger.setAssets(event.getAssets());
        this.repository.save(ledger);
    }

    @QueryHandler
    public Optional getLedger(GetLedgerQuery query) {
        return repository
                .findById(query.getId())
                .map(ledger -> new LedgerDto(ledger.getUserId(), ledger.getAssets()));
    }
}
