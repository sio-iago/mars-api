# Mars Weather API

A REST API for querying Nasa's Mars Weather API.

## Pre requisites

In order to run the project you will need:

+ Java 8
+ Maven 3
+ Nasa API key (You can get one here: https://api.nasa.gov/)

## Compiling and testing

On the project root folder, run `mvn clean install`.

## Running the api

On the project root folder, run `NASA_API_KEY=YOUR_API_KEY mvn spring-boot:run`.

Or, to run the standalone jar, navigate to the target folder and run `java -jar -Dnasa.api.key=YOUR_API_KEY mars-api-0.0.1-SNAPSHOT.jar`