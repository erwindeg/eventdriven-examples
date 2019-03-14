# Querying external system (Async, query update emitter), retry
# timeout, retry, switchMap / switchIfEmpty / switchOnFirst
# onErrorReturn
# doOnXXX
# future to mono, optional to mono
# Aggregate & Unittest
# Command / Event


Users shouldn't have to make modules, only code + tests


# Market exercises
## Exercise 1
nl.trifork.coins.market.MarketService.retrieveMarketData
Exercise 1: uncomment the toCoinDtoMono call and implement it
Hints: 
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#flatMap-java.util.function.Function-)
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#map-java.util.function.Function-)


## Exercise 2
nl.trifork.coins.restfacade.controller.MarketController
Exercise 2: map the response from the "updates" Flux to the correct response of this method
Hints:
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#next--)
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#timeout-java.time.Duration-)
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnError-java.lang.Class-java.util.function.Consumer-)
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#onErrorReturn-java.lang.Class-T-)

## Exercise 3
nl.trifork.coins.market.MarketService.retrieveMarketData
Exercise 3: we can call the retrieveSingleCoinData multiple times to return a Flux
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#fromIterable-java.lang.Iterable-)

## Exercise 4
nl.trifork.coins.market.MarketService.retrieveMarketData
Exercise 4: Subscribe to the Flux and emit each item using the queryUpdateEmitter. Don't forget to handle the exceptions. Additionally we should signal when we are done emitting items.
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#doOnComplete-java.lang.Runnable-)
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#subscribe-org.reactivestreams.Subscriber-)

## Exercise 5
nl.trifork.coins.restfacade.controller.MarketController.getMarket
Exercise 5: map the response from the "updates" Flux to the correct response of this method
(https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#collectList--)

# Quote exercises
## Exercise 6
nl.trifork.coins.market.QuoteService.generateQuote
Exercise 6: subcribe to the result of retrieveSingleCoinDataWithBaseCurrency and the send a QuoteGeneratedEvent on success and a GenerateQuoteFailedEvent on error.

# Order aggregate exercises
## Exercise 7
nl.trifork.coins.trading.command.OrderService.createOrder
Exercise 7: We should send a CreateOrderCommand for every generated quote.
(https://docs.axoniq.io/reference-guide/implementing-domain-logic/command-handling/aggregate)

## Exercise 8
nl.trifork.coins.trading.command.OrderAggregate.OrderAggregate(nl.trifork.coins.coreapi.CreateOrderCommand)
Exercise 8: send an event for this command to provide state for this aggregate through event sourcing

## Exercise 9
nl.trifork.coins.trading.command.OrderAggregate.executeOrder
Exercise 9: validate the command and send an appropriate event or throw an exception

## Exercise 10
nl.trifork.coins.trading.command.OrderAggregate.success
Exercise 10: validate the command and send an appropriate event or throw an exception

## Exercise 11
nl.trifork.coins.trading.command.OrderSaga.on(nl.trifork.coins.coreapi.OrderExecutedEvent)
Exercise 11: When an order is executed, we should try to mutate the ledger, implement the success and failure scenario's
(https://docs.axoniq.io/reference-guide/implementing-domain-logic/complex-business-transactions/implementing-saga)
