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
Exercise 1: uncomment the callExternalService call and map the response to a CoinDto

## Exercise 2
nl.trifork.coins.restfacade.controller.MarketController
Exercise 2: map the response from the "updates" Flux to the correct response of this method

## Exercise 3
nl.trifork.coins.market.MarketService.retrieveMarketData
Exercise 3: we can call the retrieveSingleCoinData multiple times to return a Flux

## Exercise 4
nl.trifork.coins.market.MarketService.retrieveMarketData
Exercise 4: add the doOnComplete

## Exercise 5

# Quote exercises
## Exercise 6
Need to explain when to subscribe and when not!!!
