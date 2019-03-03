package nl.trifork.coins.trading.query;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
public class LedgerEntity {

    @Id
    private String userId;
    @ElementCollection
    private Map<String, BigDecimal> assets = new HashMap<>();

    public LedgerEntity() {
    }

    public LedgerEntity(String userId) {
        this(userId,null);
    }

    public LedgerEntity(String userId, Map<String, BigDecimal> assets) {
        this.userId = userId;
        this.assets = assets;
    }

    public void setAssets(Map<String, BigDecimal> assets) {
        this.assets = assets;
    }

    public String getUserId() {
        return this.userId;
    }

    public Map<String,BigDecimal> getAssets() {
        return new HashMap<>(this.assets);
    }
}
