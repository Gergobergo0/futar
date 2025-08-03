# Java Futar Project

This is a multi-module Java project for a client application that interacts with an API (generated via OpenAPI) and displays data using JavaFX.

## Structure

The project contains three Maven modules:

1. `java-futar` – the parent module (packaging type: pom)
2. `FUTAR` – the main JavaFX application (uses the client module)
3. `java-client-generated` – the API client generated from an OpenAPI specification

## Requirements

- Java 17 (for the `FUTAR` module)
- Java 8 (for the `java-client-generated` module)
- Maven 3.8+
- JavaFX SDK (version 21)

## Technologies used

- JavaFX (web, swing, controls, fxml)
- iText PDF (for PDF generation)
- OpenAPI Generator (client module)
- JUnit 5 for testing
- Gson, OkHttp for JSON and HTTP
- Spotless plugin for code formatting

## Build and run

To build the whole project:

```bash
mvn clean install
```

To run the application (FUTAR module):

```bash
cd FUTAR
mvn javafx:run
```

Make sure you have JavaFX installed or configured if not using a bundled runtime.

## JavaDoc

To generate documentation:

```bash
mvn javadoc:javadoc
```

The output will be in:

```
target/site/apidocs/index.html
```

## Notes

- The `FUTAR` module is the main application. It uses JavaFX and communicates with the API through the generated client.
- The `java-client-generated` module is generated and should not be edited manually.
- The `java-futar` is the parent module and links everything together.