package com.palette.battlebattle.simulator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Attack;
import com.palette.battlebattle.simulator.card.Card;
import com.palette.battlebattle.simulator.card.CardSet;
import com.palette.battlebattle.simulator.card.CardTester;
import com.palette.battlebattle.simulator.card.CardTester.Result;

/**
 * The <code>Simulator</code> runs gameplay between tuples of cards in order to
 * verify gameplay balance.
 *
 * @author David Yee
 */
public class Simulator {
    private static final Logger LOGGER = Logger.getLogger(Simulator.class);
    private static final int NUMBER_OF_ROUNDS = 1000;

    /**
     * Runs the simulation with all cards implemented. The cards to simulate are
     * found via reflection, permutated with one another, and then simulated.
     * 
     * @param args
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        CardTester ct = new CardTester();
        Reflections reflections = new Reflections("com.palette.battlebattle.simulator.card.impl");
        Set<Class<? extends Card>> cardsFound = reflections.getSubTypesOf(Card.class);
        List<Class<? extends Card>> cardsList = new ArrayList<>(cardsFound);

        Set<CardSet> cardSets = new LinkedHashSet<>();
        for (Class<? extends Card> card : cardsList) {
            for (Class<? extends Card> card2 : cardsList) {
                CardSet cardSet = new CardSet(card, card2);
                cardSets.add(cardSet);
            }
        }

        ResultMatrix resultMatrix = new ResultMatrix(cardsList);
        if (cardSets.size() > 0) {
            LOGGER.info(
                    String.format("Running simulation for %d card sets...", cardSets.size()) + System.lineSeparator());

            /*
             * This loop runs in serial but it can run in parallel; however, if
             * you are reading the debug output it may not appear in the correct
             * order.
             */
            for (CardSet cs : cardSets) {
                Result[] results = ct.runTest(NUMBER_OF_ROUNDS, cs.getOne(), cs.getTwo());
                LOGGER.debug(ct.getFormattedOutput(results));

                int total = Stream.of(results).map(Result::getWins).mapToInt(Integer::intValue).sum();
                resultMatrix.insertResult(results[0], results[1], total);
            }
        } else {
            LOGGER.error("No cards found! Please check that card implementations exist in the correct package!");
        }

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
     * Simulates the gameplay between two given cards.
     * 
     * @param one
     *            The first card.
     * @param two
     *            The second card.
     * @return The card that won and wrapped within an optional; an empty
     *         optional otherwise.
     */
    public static Optional<Card> playCards(Card one, Card two) {
        int maxRounds = 100;
        int currentRound = 1;

        final String oneName = one.getClass().getSimpleName();
        final String twoName = two.getClass().getSimpleName();

        LOGGER.debug(String.format("%s vs %s", oneName, twoName));

        while (one.getHealth() > 0 && two.getHealth() > 0 && maxRounds > 0) {
            String identation = "  ";
            LOGGER.debug(identation + String.format("Round #%d:", currentRound));
            identation = "    ";

            LOGGER.debug(identation
                    + String.format("  %s has %d hp and %d tokens.", oneName, one.getHealth(), one.getTokens()));
            LOGGER.debug(identation
                    + String.format("  %s has %d hp and %d tokens.", twoName, two.getHealth(), two.getTokens()));

            int oneRoll = one.roll();
            int twoRoll = two.roll();

            one.applyResultOfRoll(oneRoll);
            two.applyResultOfRoll(twoRoll);

            LOGGER.debug(identation + String.format("  %s rolled a %d.", oneName, oneRoll));
            LOGGER.debug(identation + String.format("  %s rolled a %d.", twoName, twoRoll));

            Attack oneAttack = new Attack(one, oneRoll);
            Attack twoAttack = new Attack(two, twoRoll);

            // Determine who gets to go first
            Attack first = Attack.getAttackThatGoesFirst(oneAttack, twoAttack);
            Attack second;
            if (first == oneAttack) {
                second = twoAttack;
                LOGGER.trace(identation + String.format("  %s gets to go first.", oneName));
            } else {
                second = oneAttack;
                LOGGER.trace(identation + String.format("  %s gets to go first.", twoName));
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

            // Apply the post-passive abilities
            first.getCard().applyPassiveAfterEvaluation(newFirstAction, newSecondAction);
            second.getCard().applyPassiveAfterEvaluation(newSecondAction, newFirstAction);

            LOGGER.debug(identation
                    + String.format("  %s has %d hp and %d tokens.", oneName, one.getHealth(), one.getTokens()));
            LOGGER.debug(identation
                    + String.format("  %s has %d hp and %d tokens.", twoName, two.getHealth(), two.getTokens()));

            --maxRounds;
            ++currentRound;
        }

        boolean bothAlive = one.getHealth() > 0 && two.getHealth() > 0;
        boolean bothDead = one.getHealth() <= 0 && two.getHealth() <= 0;
        if (bothAlive || bothDead) {
            LOGGER.debug("Result: TIE!");
            return Optional.empty();
        } else if (one.getHealth() > 0) {
            LOGGER.debug(String.format("Result: %s WON!", oneName));
            return Optional.of(one);
        } else {
            LOGGER.debug(String.format("Result: %s WON!", twoName));
            return Optional.of(two);
        }
    }

}
