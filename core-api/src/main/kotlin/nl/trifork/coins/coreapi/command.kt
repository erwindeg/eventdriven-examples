package nl.trifork.coins.coreapi

import nl.trifork.model.CoinType
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal

data class GenerateQuoteCommand(@TargetAggregateIdentifier val id: String, val userId: String, val fromCurrency: CoinType, val toCurrency: CoinType, val amount: BigDecimal)
data class CreateLedgerCommand(@TargetAggregateIdentifier val userId: String)
data class CreateOrderCommand(@TargetAggregateIdentifier val id: String, val userId: String, val fromCurrency: CoinType, val toCurrency: CoinType, val amount: BigDecimal, val price: BigDecimal)
data class ExecuteOrderCommand(@TargetAggregateIdentifier val id: String, val userId: String)
data class SuccessOrderCommand(@TargetAggregateIdentifier val id: String)
data class FailOrderCommand(@TargetAggregateIdentifier val id: String)
data class MutateLedgerCommand(@TargetAggregateIdentifier val userId: String, val orderId : String, val fromCurrency: CoinType, val fromAmount: BigDecimal, val toCurrency: CoinType, val toAmount: BigDecimal)