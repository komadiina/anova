# ANOVA - Variance Analysis

A university project that implements the Analysis of Variance (ANOVA), a statistical method used to analyze the differences among group means in a sample.

## Project Structure

The project follows a standard Maven project structure. The main code is located under `src/main/java/org/unibl/etf/prs/`.

The main classes are:

- [`Main`](src/main/java/org/unibl/etf/prs/Main.java): The entry point of the application.
- [`Anova`](src/main/java/org/unibl/etf/prs/anova/Anova.java): The class that implements the ANOVA component.

## Building the Project

This is a Maven project, and it can be built by running the following command in the project root directory:

```sh
mvn clean install
```

## Running the Project

After building the project, you can run it with the following command:

```sh
java -cp target/anova-1.0-SNAPSHOT.jar org.unibl.etf.prs.Main
```

## Dependencies

The project uses the following dependencies:

- JUnit 3.8.1 for testing.
- Lombok 1.18.32 to reduce boilerplate code.
- JDistLib 0.4.5 for statistical distributions.