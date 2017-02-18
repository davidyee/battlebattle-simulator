package com.davidvyee.battlebattle.simulator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Attack;
import com.davidvyee.battlebattle.simulator.card.Card;
import com.davidvyee.battlebattle.simulator.card.CardSet;
import com.davidvyee.battlebattle.simulator.card.CardTester;
import com.davidvyee.battlebattle.simulator.card.CardTester.Result;

/**
 * The <code>Simulator</code> runs gameplay between tuples of cards in order to
 * verify gameplay balance.
 *
 * @author David Yee
 */
public class Simulator {
    private static final Logger LOGGER = Logger.getLogger(Simulator.class);
    private int numberOfRounds = 1000;

    private final Collection<? extends Card> cards;
    private final Set<CardSet> cardSets = new LinkedHashSet<>();

    /**
     * Simulates gameplay between cards.
     * <p>
     * The cards to simulate are found via reflection and permutated with one
     * another.
     */
    public Simulator() {
        this(Card.getCardClasses().stream().map(c -> {
            try {
                return c.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toSet()));
    }

    public Simulator(Set<? extends Card> cards) {
        this.cards = cards;

        List<? extends Card> cardsList = new ArrayList<>(cards);

        for (Card card : cardsList) {
            for (Card card2 : cardsList) {
                CardSet cardSet = new CardSet(card, card2);
                cardSets.add(cardSet);
            }
        }
    }

    /**
     * Performs a simulation and retrieves the result matrix.
     * 
     * @return The results of the simulation for each combination of cards.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ResultMatrix runSimulation() throws InstantiationException, IllegalAccessException {
        ResultMatrix resultMatrix = new ResultMatrix(cards);
        if (cardSets.size() > 0) {
            LOGGER.info(
                    String.format("Running simulation for %d card sets...", cardSets.size()) + System.lineSeparator());

            /*
             * This loop runs in serial but it can run in parallel; however, if
             * you are reading the debug output it may not appear in the correct
             * order.
             */
            for (CardSet cs : cardSets) {
                Result[] results = CardTester.runTest(numberOfRounds, cs.getOne(), cs.getTwo());
                LOGGER.debug(CardTester.getFormattedOutput(results));

                int total = Stream.of(results).map(Result::getWins).mapToInt(Integer::intValue).sum();
                resultMatrix.insertResult(results[0], results[1], total);
            }
        } else {
            LOGGER.error("No cards found! Please check that card implementations exist in the correct package!");
        }

        return resultMatrix;
    }

    /**
     * Output the results. Outputs to stdout when logging is enabled. Also
     * outputs to a results CSV file with the current date and time keyed into
     * the file name.
     * 
     * @param resultMatrix
     *            The result matrix from a simulation that was previously run.
     */
    public void outputResults(ResultMatrix resultMatrix) {
        LOGGER.info(resultMatrix.getPrettyMatrix());

        // Output to file
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();

        Path path = Paths.get(String.format("result-%s.csv", dtf.format(now)));
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(resultMatrix.getPrettyMatrix());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the simulation with all cards implemented.
     * 
     * @param args
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Simulator simulator = new Simulator();
        ResultMatrix results = simulator.runSimulation();
        simulator.outputResults(results);
    }

    /**
     * Simulates the gameplay between two given cards.
     * 
     * @param one
     *            The first card.
     * @param two
     *            The second card.
     * @return The card that won and wrapped within an optional; an empty
     *         optional otherwise.
     */
    public static Optional<Card> playCards(final Card one, final Card two) {
        int maxRounds = 100;
        int currentRound = 1;

        final String oneName = one.getClass().getSimpleName();
        final String twoName = two.getClass().getSimpleName();

        LOGGER.debug(String.format("%s vs %s", oneName, twoName));

        /*
         * Below are mutable references thereby allowing cards to mutate during
         * the dual; however, we keep the original card references and names for
         * the sake of the final simulation result.
         */
        Card roundCardOne = one;
        String roundCardOneName = oneName;
        Card roundCardTwo = two;
        String roundCardTwoName = twoName;

        while (roundCardOne.getHealth() > 0 && roundCardTwo.getHealth() > 0 && maxRounds > 0) {
            String identation = "  ";
            LOGGER.debug(identation + String.format("Round #%d:", currentRound));
            identation = "    ";

            LOGGER.debug(identation + String.format("  %s has %d hp and %d tokens.", roundCardOneName,
                    roundCardOne.getHealth(), roundCardOne.getTokens()));
            LOGGER.debug(identation + String.format("  %s has %d hp and %d tokens.", roundCardTwoName,
                    roundCardTwo.getHealth(), roundCardTwo.getTokens()));

            int oneRoll = roundCardOne.roll();
            int twoRoll = roundCardTwo.roll();

            roundCardOne.applyResultOfRoll(oneRoll);
            roundCardTwo.applyResultOfRoll(twoRoll);

            LOGGER.debug(identation + String.format("  %s rolled a %d.", roundCardOneName, oneRoll));
            LOGGER.debug(identation + String.format("  %s rolled a %d.", roundCardTwoName, twoRoll));

            Attack oneAttack = new Attack(roundCardOne, oneRoll);
            Attack twoAttack = new Attack(roundCardTwo, twoRoll);

            // Determine who gets to go first
            Attack first = Attack.getAttackThatGoesFirst(oneAttack, twoAttack);
            Attack second;
            if (first == oneAttack) {
                second = twoAttack;
                LOGGER.trace(identation + String.format("  %s gets to go first.", roundCardOneName));
            } else {
                second = oneAttack;
                LOGGER.trace(identation + String.format("  %s gets to go first.", roundCardTwoName));
            }

            // Generate initial actions
            Action firstAction = new Action(true, first);
            Action secondAction = new Action(false, second);

            // Apply the pre-passive abilities
            first.getCard().applyPassiveBeforeEvaluation(firstAction, secondAction);
            second.getCard().applyPassiveBeforeEvaluation(secondAction, firstAction);

            // Modify actions based on tokens
            Action newFirstAction = first.getCard().getBestAction(firstAction, secondAction);
            // apply the +1 token if necessary
            first.getCard().applyResultOfRoll(newFirstAction.getAttack());
            Action newSecondAction = second.getCard().getBestAction(secondAction, newFirstAction);
            // apply the +1 token if necessary
            second.getCard().applyResultOfRoll(newSecondAction.getAttack());

            // Apply the actions
            Action.evaluateActions(newFirstAction, newSecondAction);

            // Check if the cards have changed during the evaluation
            // This clause supports morphing card abilities
            if (newFirstAction.getMorphCard().isPresent()) {
                Card morphedCard = newFirstAction.getMorphCard().get();
                if (roundCardOne == newFirstAction.getCard()) {
                    roundCardOne = morphedCard;
                    roundCardOneName = morphedCard.getClass().getSimpleName();
                } else {
                    roundCardTwo = morphedCard;
                    roundCardTwoName = morphedCard.getClass().getSimpleName();
                }
            }

            if (newSecondAction.getMorphCard().isPresent()) {
                Card morphedCard = newSecondAction.getMorphCard().get();
                if (roundCardOne == newSecondAction.getCard()) {
                    roundCardOne = morphedCard;
                    roundCardOneName = morphedCard.getClass().getSimpleName();
                } else {
                    roundCardTwo = morphedCard;
                    roundCardTwoName = morphedCard.getClass().getSimpleName();
                }
            }

            // Apply the post-passive abilities
            first.getCard().applyPassiveAfterEvaluation(newFirstAction, newSecondAction);
            second.getCard().applyPassiveAfterEvaluation(newSecondAction, newFirstAction);

            LOGGER.debug(identation + String.format("  %s has %d hp and %d tokens.", roundCardOneName,
                    roundCardOne.getHealth(), roundCardOne.getTokens()));
            LOGGER.debug(identation + String.format("  %s has %d hp and %d tokens.", roundCardTwoName,
                    roundCardTwo.getHealth(), roundCardTwo.getTokens()));

            --maxRounds;
            ++currentRound;
        }

        boolean bothAlive = roundCardOne.getHealth() > 0 && roundCardTwo.getHealth() > 0;
        boolean bothDead = roundCardOne.getHealth() <= 0 && roundCardTwo.getHealth() <= 0;
        if (bothAlive || bothDead) {
            LOGGER.debug("Result: TIE!");
            return Optional.empty();
        } else if (roundCardOne.getHealth() > 0) {
            LOGGER.debug(String.format("Result: %s WON!", oneName));
            return Optional.of(one);
        } else {
            LOGGER.debug(String.format("Result: %s WON!", twoName));
            return Optional.of(two);
        }
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

}
