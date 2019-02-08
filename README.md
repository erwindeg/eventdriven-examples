# eventdriven-examples
Reactive, event driven microservices in Java

## Start axon server
docker/docker-compose up

## Run app as a monolith
Run monolith/Main class

## Run app as  microservices
Use the main classes of the different modules:
e.g. Run rest-facade/RestFacadeApp class

## build all modules
gradle clean build

## building docker images
gradle buildDockerImage