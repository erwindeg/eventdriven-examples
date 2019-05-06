# eventdriven-examples
Reactive, event driven microservices in Java

## Start axon server
```sh
docker-compose -f docker/docker-compose.yml up
```

## Run app as a monolith
Run the main class  of the `monolith` module in your IDE.

| module     | class                            | path                                                         |
|:-----------|:---------------------------------|:-------------------------------------------------------------|
| `monolith` | `nl.trifork.coins.monolith.Main` | `monolith/src/main/java/nl/trifork/coins/monolith/Main.java` |


## Run app as microservices
Run the main class of each of the modules individually:

| module        | class                                       | path                                                                       |
|:--------------|:--------------------------------------------|:---------------------------------------------------------------------------|
| `market`      | `nl.trifork.coins.market.MarketApp`         | `market/src/main/java/nl/trifork/coins/market/MarketApp.java`              |
| `trading`     | `nl.trifork.coins.trading.TradingApp`       | `trading/src/main/java/nl/trifork/coins/trading/TradingApp.java`           |
| `rest-facade` | `nl.trifork.coins.restfacade.RestFacadeApp` | `rest-facade/src/main/java/nl/trifork/coins/restfacade/RestFacadeApp.java` |


Alternatively you could build jars and run them from the command line:
```sh
./gradlew clean build jar
java -jar market/build/libs/market.jar 
java -jar trading/build/libs/trading.jar 
java -jar rest-facade/build/libs/rest-facade.jar 
```

## Build all modules
with tests:
```sh
./gradlew clean build
```
without tests:
```sh
./gradlew clean build -x test
```

## Building docker images
```sh
./gradlew buildDockerImage
```

## Load docker image from USB stick
```sh
docker image load -i axonserver.tar
```