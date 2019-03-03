package nl.trifork.coins.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal

data class GenerateQuoteCommand(@TargetAggregateIdentifier val id: String, val userId: String, val fromCurrency: String, val toCurrency: String, val amount: BigDecimal)
data class CreateLedgerCommand(@TargetAggregateIdentifier val userId: String)
data class CreateOrderCommand(@TargetAggregateIdentifier val id: String, val userId: String, val fromCurrency: String, val toCurrency: String, val amount: BigDecimal, val price: BigDecimal)
data class ExecuteOrderCommand(@TargetAggregateIdentifier val id: String, val userId: String)
data class SuccessOrderCommand(@TargetAggregateIdentifier val id: String)
data class FailOrderCommand(@TargetAggregateIdentifier val id: String)
data class MutateLedgerCommand(@TargetAggregateIdentifier val userId: String, val fromCurrency: String, val fromAmount: BigDecimal, val toCurrency: String, val toAmount: BigDecimal)