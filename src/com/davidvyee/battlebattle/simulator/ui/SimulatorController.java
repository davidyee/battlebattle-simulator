package com.davidvyee.battlebattle.simulator.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.davidvyee.battlebattle.simulator.ResultMatrix;
import com.davidvyee.battlebattle.simulator.Simulator;
import com.davidvyee.battlebattle.simulator.card.Card;
import com.davidvyee.battlebattle.simulator.ui.card.CardDto;
import com.davidvyee.battlebattle.simulator.ui.card.CardsTableController;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class SimulatorController {
    @FXML
    private Button btnSimulate;
    private String btnSimulateOriginalText;
    private BarChart<String, Number> barChart;

    @FXML
    private BorderPane bpLeft;
    @FXML
    private BorderPane bpRight;

    @FXML
    private Hyperlink hDavidWebsite;
    @FXML
    private TextField tfNumberOfSimulations;
    @FXML
    private Text txtTitle;

    private List<CardDto> cards;
    private CardsTableController cardsTableController;

    public void initialize() throws IOException, InstantiationException, IllegalAccessException {
        btnSimulateOriginalText = btnSimulate.getText();

        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());

        Parent parent = createZoomPane(barChart);
        parent.setStyle("-fx-background-color:transparent;");
        bpRight.setCenter(parent);

        NumberAxis yAxis = ((NumberAxis) barChart.getYAxis());
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(105);

        Set<Class<? extends Card>> cardsSet = Card.getCardClasses();
        cards = new ArrayList<>(cardsSet.size());

        List<CardDto> customCards = new ArrayList<>();

        for (Class<? extends Card> card : cardsSet) {
            Card newCard = card.newInstance();
            CardDto newCardDto = new CardDto(newCard);

            String cardName = newCard.getClass().getSimpleName();
            if (cardName.endsWith("Custom") && !cardName.equals("Custom")) {
                customCards.add(newCardDto);
            }

            cards.add(newCardDto);
        }

        // Disable the original if a custom implementation is found
        for (CardDto cardDto : customCards) {
            Card newCard = cardDto.getCard();
            String cardName = newCard.getClass().getSimpleName();
            String parentCardName = cardName.substring(0, cardName.lastIndexOf("Custom"));
            cards.stream().filter(cd -> cd.getCard().getClass().getSimpleName().equals(parentCardName)).findAny()
                    .ifPresent(parentCard -> {
                        parentCard.simulateProperty().set(false);
                    });
        }

        Collections.sort(cards, (card1, card2) -> {
            return card1.getClass().getSimpleName().compareTo(card2.getClass().getSimpleName());
        });

        FXMLLoader cardPaneLoader = new FXMLLoader(getClass().getResource("card/CardsTable.fxml"));
        Node node = cardPaneLoader.load();
        cardsTableController = cardPaneLoader.getController();
        cardsTableController.setCards(cards);

        bpLeft.setCenter(node);
        SplitPane.setResizableWithParent(bpLeft, false);

        hDavidWebsite.setOnAction((event) -> {
            SimulatorApplication.APPLICATION.getHostServices().showDocument("http://www.davidvyee.com/");
        });

        tfNumberOfSimulations.setText("100");
        tfNumberOfSimulations.setTextFormatter(new TextFormatter<String>(change -> {
            /*
             * Formatting idea from ShadowEagle
             * (http://stackoverflow.com/users/6436041/shadoweagle)
             * http://stackoverflow.com/q/40472668/2557554 CC-BY-SA 3.0
             * (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
             */
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        }));
    }

    /*
     * Based on code written by Håvard Geithus
     * (http://stackoverflow.com/users/838608/h%c3%a5vard-geithus)
     * http://stackoverflow.com/a/23518314/2557554 CC-BY-SA 3.0
     * (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
     */
    public void centerNodeInScrollPane(ScrollPane scrollPane, ScrollEvent event) {
        double h = scrollPane.getContent().getBoundsInLocal().getHeight();
        double y = event.getY();
        double v = scrollPane.getViewportBounds().getHeight();
        scrollPane.setVvalue(Math.min(scrollPane.getVmax() * ((y - 0.5 * v) / (h - v)), scrollPane.getVmax()));

        double w = scrollPane.getContent().getBoundsInLocal().getWidth();
        double x = event.getX();
        double v2 = scrollPane.getViewportBounds().getWidth();
        scrollPane.setHvalue(Math.min(scrollPane.getHmax() * ((x - 0.5 * v2) / (w - v2)), scrollPane.getHmax()));
    }

    /*
     * Based on code written by jewelsea
     * (http://stackoverflow.com/users/1155209/jewelsea)
     * http://stackoverflow.com/a/16682180/2557554 and licensed under CC-BY-SA
     * 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
     */
    private Parent createZoomPane(final BarChart<String, Number> group) {
        final double SCALE_DELTA = 1.1;
        final StackPane zoomPane = new StackPane();

        zoomPane.getChildren().add(group);

        final ScrollPane scroller = new ScrollPane();
        final Group scrollContent = new Group(zoomPane);
        scroller.setContent(scrollContent);

        scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            }
        });

        scroller.setPrefViewportWidth(256);
        scroller.setPrefViewportHeight(256);

        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;

                group.setPrefWidth(group.getWidth() * scaleFactor);

                Platform.runLater(() -> {
                    scroller.layout();
                    // scrollContent.layout();
                    Platform.runLater(() -> {
                        centerNodeInScrollPane(scroller, event);
                    });
                });
            }
        });

        // Panning via drag....
        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
        scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
            }
        });

        scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double deltaX = event.getX() - lastMouseCoordinates.get().getX();
                double extraWidth = scrollContent.getLayoutBounds().getWidth()
                        - scroller.getViewportBounds().getWidth();
                double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
                double desiredH = scroller.getHvalue() - deltaH;
                scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

                double deltaY = event.getY() - lastMouseCoordinates.get().getY();
                double extraHeight = scrollContent.getLayoutBounds().getHeight()
                        - scroller.getViewportBounds().getHeight();
                double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
                double desiredV = scroller.getVvalue() - deltaV;
                scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
            }
        });

        return scroller;
    }

    public void runSimulation(ActionEvent event) {
        Task<ResultMatrix> task = new Task<ResultMatrix>() {

            @Override
            protected ResultMatrix call() throws Exception {
                Simulator simulator = new Simulator(cardsTableController.getCards().stream().map(CardDto::getCard)
                        .filter(c -> c.isSimulate()).collect(Collectors.toSet()));
                simulator.setNumberOfRounds(Integer.valueOf(tfNumberOfSimulations.getText()));
                ResultMatrix results = simulator.runSimulation();
                simulator.outputResults(results);
                return results;
            }

        };
        task.setOnSucceeded(taskFinishEvent -> {
            ResultMatrix results = (ResultMatrix) taskFinishEvent.getSource().getValue();
            barChart.getData().setAll(results.getBarChartSeries(false));
            barChart.setLegendVisible(false);

            BarChart<String, Number> barChartTmp = new BarChart<>(new CategoryAxis(), new NumberAxis());
            barChartTmp.getData().setAll(results.getBarChartSeries(true));

            adjustColours(barChart);
            adjustColours(barChartTmp);

            bpRight.setBottom(barChartTmp.lookup(".chart-legend"));

            btnSimulate.setDisable(false);
            btnSimulate.setText(btnSimulateOriginalText);
            txtTitle.setText(
                    String.format("Percentage Wins Against Other Cards (%s Trials)", tfNumberOfSimulations.getText()));
        });
        task.setOnFailed(taskFailedEvent -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Simulation Failed");
            alert.setContentText("Please try again!");
            alert.show();

            btnSimulate.setDisable(false);
            btnSimulate.setText(btnSimulateOriginalText);
        });
        task.setOnRunning(taskRunningEvent -> {
            btnSimulate.setDisable(true);
            btnSimulate.setText("Please wait...");
        });
        new Thread(task).start();
    }

    // Color index starts at zero
    private static final int NUMBER_OF_COLOR_STYLES = 18;

    // Method based on code from
    // http://tiwulfx.panemu.com/2013/01/07/provide-more-colors-for-chart-series/
    private void adjustColours(BarChart<?, ?> chart) {
        /*
         * Set Series color
         */
        for (int i = 0; i < chart.getData().size(); i++) {
            for (Node node : chart.lookupAll(".series" + i)) {
                node.getStyleClass().remove("default-color" + (i % 8));
                node.getStyleClass().add("default-color" + (i % NUMBER_OF_COLOR_STYLES));
            }
        }

        /*
         * Set Legend items color
         */
        int i = 0;
        for (Node node : chart.lookupAll(".chart-legend-item")) {
            if (node instanceof Label && ((Label) node).getGraphic() != null) {
                ((Label) node).getGraphic().getStyleClass().remove("default-color" + (i % 8));
                ((Label) node).getGraphic().getStyleClass().add("default-color" + (i % NUMBER_OF_COLOR_STYLES));
            }
            i++;
        }

    }

}
