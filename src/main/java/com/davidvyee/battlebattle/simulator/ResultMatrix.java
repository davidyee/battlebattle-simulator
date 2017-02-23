package com.davidvyee.battlebattle.simulator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.davidvyee.battlebattle.simulator.card.Card;
import com.davidvyee.battlebattle.simulator.card.CardTester.Result;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

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

    public ResultMatrix(Collection<? extends Card> cards) {
        cardNames = cards.stream().map(c -> c.getClass().getSimpleName()).sorted().collect(Collectors.toList());
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
        column[rowPos] = String.format("%.2f", (double) one.getWins() / totalGames * 100f);

        // Second result
        ResultMatrixResult secondResult = cardResults.get(rowPos);
        secondResult.totalGames += totalGames;
        secondResult.totalWins += two.getWins();
        String[] column2 = secondResult.data;
        column2[colPos] = String.format("%.2f", (double) two.getWins() / totalGames * 100f);
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
                    + String.format("%.2f", (double) result.totalWins / result.totalGames * 100f));

            for (int j = 0; j < cardResult.length; ++j) {
                output.append("," + cardResult[j]);
            }
            output.append(System.lineSeparator());
        }

        return output.toString();
    }

    public List<XYChart.Series<String, Number>> getBarChartSeries(boolean showAverage) {
        List<XYChart.Series<String, Number>> series = new ArrayList<>();
        for (int i = 0; i < cardResults.size(); ++i) {
            ResultMatrixResult result = cardResults.get(i);
            XYChart.Series<String, Number> newSeries = new XYChart.Series<>();

            String seriesName;
            if (showAverage) {
                seriesName = cardNames.get(i)
                        + String.format(" (%.2f%%)", (double) result.totalWins / result.totalGames * 100f);
            } else {
                seriesName = cardNames.get(i);
            }
            
            newSeries.setName(seriesName);

            for (int j = 0; j < result.data.length; ++j) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(cardNames.get(j),
                        new BigDecimal(cardResults.get(j).data[i]));
                data.nodeProperty().addListener(new ChangeListener<Node>() {
                    @Override
                    public void changed(ObservableValue<? extends Node> ov, Node oldNode, final Node node) {
                        if (node != null) {
                            displayLabelForData(data);
                        }
                    }
                });
                newSeries.getData().add(data);
            }

            series.add(newSeries);
        }
        return series;
    }

    /*
     * Based on code written by jewelsea
     * (http://stackoverflow.com/users/1155209/jewelsea)
     * http://stackoverflow.com/a/15375168/2557554 and licensed under CC-BY-SA
     * 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
     */
    private void displayLabelForData(XYChart.Data<String, Number> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getYValue() + "");

        ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                dataText.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2));
                dataText.setLayoutY(Math.round(bounds.getMinY() - dataText.prefHeight(-1) * 0.5));
            }
        };

        ChangeListener<Parent> parentChangeListener = new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
                Group parentGroup = (Group) parent;
                if (parentGroup == null) {
                    node.parentProperty().removeListener(this);
                    node.boundsInParentProperty().removeListener(boundsChangeListener);
                    Platform.runLater(() -> ((Group) oldParent).getChildren().remove(dataText));
                } else {
                    parentGroup.getChildren().add(dataText);
                }
            }
        };

        node.parentProperty().addListener(parentChangeListener);
        node.boundsInParentProperty().addListener(boundsChangeListener);
    }
}
