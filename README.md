# eventdriven-examples
Reactive, event driven microservices in Java

## Start axon server
```sh
docker-compose -f docker/docker-compose.yml up -d
```

## Run app as a monolith
```sh
./gradlew clean build jar
java -jar monolith/build/libs/monolith.jar 
```

## Run app as microservices
```sh
./gradlew clean build jar
java -jar market/build/libs/market.jar 
java -jar trading/build/libs/trading.jar 
java -jar rest-facade/build/libs/rest-facade.jar 
```

## Build all modules
without tests:
```sh
./gradlew clean build -x test
```
with tests:
```sh
./gradlew clean build
```

## Building docker images
```sh
./gradlew buildDockerImage
```
