package nl.trifork.coins.coreapi

data class GetCoinQuery(val id: String)
data class GetQuoteQuery(val id: String)
data class GetQuoteResponse(val quote : QuoteDto?, val error : String?)
data class GetOrderQuery(val id: String)
data class GetOrderResponse(val order : OrderDto?, val error : String?)
data class GetLedgerQuery(val id: String)