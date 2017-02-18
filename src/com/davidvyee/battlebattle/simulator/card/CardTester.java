package com.davidvyee.battlebattle.simulator.card;

import java.util.Optional;
import java.util.stream.Stream;

import com.davidvyee.battlebattle.simulator.Simulator;

public class CardTester {

    /**
     * A convenience class to store the relationship between a name and the
     * number of wins it obtained.
     *
     * @author David Yee
     */
    public static class Result {
        private final String name;
        private final int wins;

        public Result(String name, int wins) {
            this.name = name;
            this.wins = wins;
        }

        @Override
        public String toString() {
            return String.format("%s: %d", name, wins);
        }

        public String getName() {
            return name;
        }

        public int getWins() {
            return wins;
        }
    }

    public static Result[] runTest(int numGames, Card card1, Card card2) {
        Optional<Card> maybeResult;

        int card1Wins = 0;
        int card2Wins = 0;
        int ties = 0;
        for (int i = 0; i < numGames; ++i) {
            Card c1 = card1.copy();
            Card c2 = card2.copy();

            maybeResult = Simulator.playCards(c1, c2);
            if (maybeResult.isPresent()) {
                Card result = maybeResult.get();
                if (result == c1)
                    ++card1Wins;
                else if (result == c2)
                    ++card2Wins;
            } else {
                ++ties;
            }
        }

        Result[] results = { new Result(card1.getClass().getSimpleName(), card1Wins),
                new Result(card2.getClass().getSimpleName(), card2Wins), new Result("Ties", ties) };
        return results;
    }

    /**
     * Formats the results in a nice way.
     * 
     * @param results
     *            A list of results to print out.
     * @return A formatted string of the results, including the percentage of
     *         wins.
     */
    public static String getFormattedOutput(Result... results) {
        StringBuilder sb = new StringBuilder(64);
        int total = Stream.of(results).map(r -> r.wins).mapToInt(Integer::intValue).sum();

        sb.append(String.format("Results (%d games): ", total) + System.lineSeparator());
        for (Result r : results) {
            sb.append("  " + r.toString());
            sb.append(String.format(" (%.2f%%) ", (double) r.wins / total * 100f));
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }
}
