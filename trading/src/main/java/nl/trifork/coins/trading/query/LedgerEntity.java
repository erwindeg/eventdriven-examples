package nl.trifork.coins.trading.query;

import nl.trifork.model.CoinType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
public class LedgerEntity {

    @Id
    private String userId;
    @ElementCollection
    private Map<CoinType, BigDecimal> assets = new HashMap<>();

    public LedgerEntity() {
    }

    public LedgerEntity(String userId) {
        this(userId,null);
    }

    public LedgerEntity(String userId, Map<CoinType, BigDecimal> assets) {
        this.userId = userId;
        this.assets = assets;
    }

    public void setAssets(Map<CoinType, BigDecimal> assets) {
        this.assets = assets;
    }

    public String getUserId() {
        return this.userId;
    }

    public Map<CoinType,BigDecimal> getAssets() {
        return new HashMap<>(this.assets);
    }
}
