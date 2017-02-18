package com.davidvyee.battlebattle.simulator.ui.card;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class CardsTableController {
    @FXML
    private TableView<CardDto> table;
    @FXML
    private TableColumn<CardDto, String> tcName;
    @FXML
    private TableColumn<CardDto, Integer> tcHealth;
    @FXML
    private TableColumn<CardDto, Integer> tcTokens;
    @FXML
    private TableColumn<CardDto, Boolean> tcSimulate;
    private final CheckBox tcSimulateSelectAll = new CheckBox();

    private SortedList<CardDto> cards;

    public void initialize() {
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tcHealth.setCellFactory(TextFieldTableCell.<CardDto, Integer>forTableColumn(new IntegerStringConverter()));
        tcHealth.setCellValueFactory(new PropertyValueFactory<>("health"));

        tcTokens.setCellFactory(TextFieldTableCell.<CardDto, Integer>forTableColumn(new IntegerStringConverter()));
        tcTokens.setCellValueFactory(new PropertyValueFactory<>("tokens"));

        tcSimulateSelectAll.setOnAction((ActionEvent event) -> {
            cards.forEach(c -> c.simulateProperty().set(tcSimulateSelectAll.isSelected()));
        });
        tcSimulate.setGraphic(tcSimulateSelectAll);
        tcSimulate.setCellFactory(CheckBoxTableCell.forTableColumn(tcSimulate));
        tcSimulate.setCellValueFactory(new PropertyValueFactory<>("simulate"));
        
        table.getSortOrder().add(tcName);
    }

    public void setCards(List<CardDto> cards) {
        this.cards = new SortedList<CardDto>(FXCollections.observableList(cards));
        this.cards.forEach(card -> card.simulateProperty()
                .addListener((observable, oldValue, newValue) -> updateSelectAllButton()));
        this.cards.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(this.cards);
        
        updateSelectAllButton();
    }

    private void updateSelectAllButton() {
        if (this.cards.stream().allMatch(c -> c.simulateProperty().get())) {
            tcSimulateSelectAll.setSelected(true);
            tcSimulateSelectAll.setIndeterminate(false);
        } else if (this.cards.stream().anyMatch(c -> c.simulateProperty().get())) {
            tcSimulateSelectAll.setIndeterminate(true);
        } else {
            tcSimulateSelectAll.setSelected(false);
            tcSimulateSelectAll.setIndeterminate(false);
        }
    }

    public ObservableList<CardDto> getCards() {
        return cards;
    }
}
