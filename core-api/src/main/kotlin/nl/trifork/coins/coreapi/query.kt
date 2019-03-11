package nl.trifork.coins.coreapi

import nl.trifork.model.CoinType

data class GetCoinQuery(val coinType: CoinType)
data class GetCoinsQuery(val ids: List<CoinType>)
data class GetQuoteQuery(val id: String)
data class GetQuoteResponse(val quote: QuoteDto)
data class GetOrderQuery(val id: String)
data class GetLedgerQuery(val id: String)