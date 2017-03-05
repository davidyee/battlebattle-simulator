# BattleBattle Simulator

A Java simulator for the BattleBattle card game.

![Simulator JavaFX Application](screenshot1.png?raw=true)

![Simulator JavaFX Application](screenshot2.png?raw=true)

## Requirements

* Java 1.8+

* Maven 

## Usage

There are two main programs in this project:

1. Run the main program in SimulatorApplication.java to run the simulator with a friendly user-interface designed in JavaFX. This interface allows you to change the health and tokens of all the cards that were found on runtime in the `com.davidvyee.battlebattlesimulator.card.impl` package. You may also customize the number of simulations to run for each combination.

2. Run the main program in Simulator.java to run each combination of cards in `com.davidvyee.battlebattle.simulator.card.impl`. The simulator outputs a results CSV file in the same directory as the program and also outputs the CSV content to standard output (stdout). By default, this program will run 1000 simulations per combination.

### Packaging the JavaFX Application

Using Maven, you can compile and package the simulator into an executable JavaFX JAR and native executable file by running the following command:

```xml
mvn jfx:jar jfx:native
```

The executable JAR and native application will be located under `target\jfx`.

### Adding New Cards

Add new cards into the `com.davidvyee.battlebattle.simulator.card.impl` package and override the necessary methods and hooks to achieve the desired card stats and abilities. Cards that are added to this package are found on runtime via Java Reflection.

### Debugging

To debug a new card, create a new JUnit test for the card and change the log4j log level to DEBUG or TRACE:

```java
log4j.rootLogger=TRACE, Console
log4j.logger.com.palette.battlebattle.simulator.card.Card=TRACE, cardappender
```

## Assumptions and Limitations

The Simulator currently has the following limitations:

* The simulation applies token-requiring powers only once. In other words, cards cannot pay for a power twice in order to apply the ability twice in a given round.

* The simulation does not evaluate the sensibility of a round in the context of possible future rounds. It only considers whether an action can produce a winnable outcome for the current round and if a token should be spent or not.

