package com.davidvyee.battlebattle.simulator.ui.card;

import com.davidvyee.battlebattle.simulator.card.Card;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CardDto {
    private final Card card;
    private StringProperty name;
    private IntegerProperty health;
    private IntegerProperty tokens;
    private BooleanProperty simulate;

    public CardDto(Card card) {
        this.card = card;

        name = new SimpleStringProperty(card.getClass().getSimpleName());
        health = new SimpleIntegerProperty(card.getHealth()) {
            @Override
            public void set(int newValue) {
                super.set(newValue);
                card.setHealth(newValue);
            }
        };
        tokens = new SimpleIntegerProperty(card.getTokens()) {
            @Override
            public void set(int newValue) {
                super.set(newValue);
                card.setTokens(newValue);
            }
        };
        simulate = new SimpleBooleanProperty(card.isSimulate()) {
            @Override
            public void set(boolean newValue) {
                super.set(newValue);
                card.setSimulate(newValue);
            }
        };
    }

    public Card getCard() {
        return card;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty healthProperty() {
        return health;
    }

    public IntegerProperty tokensProperty() {
        return tokens;
    }

    public BooleanProperty simulateProperty() {
        return simulate;
    }

}
