package nl.trifork.coins.trading.query;

import nl.trifork.coins.coreapi.OrderStatus;
import nl.trifork.model.CoinType;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class OrderEntity {

    @Id
    private String id;
    private String userId;
    private CoinType fromCurrency;
    private CoinType toCurrency;
    private BigDecimal amount;
    private BigDecimal price;
    private OrderStatus status;

    public OrderEntity(String id, String userId, CoinType fromCurrency, CoinType toCurrency, BigDecimal amount, BigDecimal price, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.price = price;
        this.status = status;
    }

    public OrderEntity() {
    }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CoinType getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(CoinType fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public CoinType getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(CoinType toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
