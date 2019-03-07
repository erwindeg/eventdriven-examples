package nl.trifork.coins.coreapi

data class GetCoinQuery(val id: String)
data class GetCoinsQuery(val ids: List<String>)
data class GetQuoteQuery(val id: String)
data class GetQuoteResponse(val quote : QuoteDto)
data class GetOrderQuery(val id: String)
data class GetLedgerQuery(val id: String)