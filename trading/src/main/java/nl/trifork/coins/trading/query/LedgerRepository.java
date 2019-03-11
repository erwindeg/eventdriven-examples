package nl.trifork.coins.trading.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<LedgerEntity, String> {
}
