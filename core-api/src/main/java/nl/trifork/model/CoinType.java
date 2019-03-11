package nl.trifork.model;

public enum CoinType {
    EUR(""),
    BTC("1"),
    ETH("2");

    private String coinId;

    CoinType(String coinId) {
        this.coinId = coinId;
    }

    public String getCoinId() {
        return coinId;
    }
}
