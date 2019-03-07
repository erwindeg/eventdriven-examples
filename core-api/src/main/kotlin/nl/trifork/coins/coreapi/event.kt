package nl.trifork.coins.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal

data class LedgerCreatedEvent(@TargetAggregateIdentifier val userId: String, val assets : Map<String, BigDecimal>)
data class QuoteGeneratedEvent(val id: String, val userId: String, val fromCurrency: String, val toCurrency: String, val amount: BigDecimal, val price: BigDecimal)
data class GenerateQuoteFailedEvent(val id: String, val cause: Throwable)
data class OrderCreatedEvent(@TargetAggregateIdentifier val id: String, val userId: String, val fromCurrency: String, val toCurrency: String, val amount: BigDecimal, val price: BigDecimal)
data class OrderExecutedEvent(@TargetAggregateIdentifier val id: String, val userId: String, val fromCurrency: String, val toCurrency: String, val amount: BigDecimal, val price: BigDecimal)
data class OrderSuccessEvent(@TargetAggregateIdentifier val id: String)
data class OrderFailedEvent(@TargetAggregateIdentifier val id: String)
data class LedgerMutatedEvent(@TargetAggregateIdentifier val userId: String, val assets : Map<String, BigDecimal>)