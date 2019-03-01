package nl.trifork.coins.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier

data class CreateVersionCommand(@TargetAggregateIdentifier val id: String)
data class VersionCreatedEvent(val id: String)