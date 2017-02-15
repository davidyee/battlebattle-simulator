package com.palette.battlebattle.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.palette.battlebattle.simulator.card.Card;
import com.palette.battlebattle.simulator.card.CardTester.Result;

public class ResultMatrix {
    private List<String> cardNames;
    private List<ResultMatrixResult> cardResults;

    private class ResultMatrixResult {
        private int totalWins = 0;
        private int totalGames = 0;
        private final String[] data;

        public ResultMatrixResult(String[] data) {
            this.data = data;
        }
    }

    public ResultMatrix(List<Class<? extends Card>> cards) {
        cardNames = cards.stream().map(Class::getSimpleName).collect(Collectors.toList());
        cardResults = new ArrayList<>(cardNames.size());

        for (int i = 0; i < cardNames.size(); ++i) {
            cardResults.add(new ResultMatrixResult(new String[cardNames.size()]));
        }
    }

    public void insertResult(Result one, Result two, int totalGames) {
        int colPos = getPositionOfCard(one.getName());
        int rowPos = getPositionOfCard(two.getName());

        // First result
        ResultMatrixResult firstResult = cardResults.get(colPos);
        firstResult.totalGames += totalGames;
        firstResult.totalWins += one.getWins();
        String[] column = firstResult.data;
        column[rowPos] = String.format("%.2f%%", (double) one.getWins()/totalGames * 100f);

        // Second result
        ResultMatrixResult secondResult = cardResults.get(rowPos);
        secondResult.totalGames += totalGames;
        secondResult.totalWins += two.getWins();
        String[] column2 = secondResult.data;
        column2[colPos] = String.format("%.2f%%", (double) two.getWins()/totalGames * 100f);
    }

    private int getPositionOfCard(String name) {
        return cardNames.indexOf(name);
    }

    public String getPrettyMatrix() {
        StringBuilder output = new StringBuilder("Card,Win Rate");
        for (String name : cardNames) {
            output.append("," + name);
        }

        output.append(System.lineSeparator());

        for (int i = 0; i < cardResults.size(); ++i) {
            ResultMatrixResult result = cardResults.get(i);
            String[] cardResult = result.data;
            output.append(cardNames.get(i) + ","
                    + String.format("%.2f%%", (double) result.totalWins / result.totalGames * 100f));

            for (int j = 0; j < cardResult.length; ++j) {
                output.append("," + cardResult[j]);
            }
            output.append(System.lineSeparator());
        }

        return output.toString();
    }
}
