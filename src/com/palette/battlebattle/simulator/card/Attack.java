package com.palette.battlebattle.simulator.card;

public class Attack {
    private Card card;
    private int roll;

    public Attack(Card card, int roll) {
        this.card = card;
        this.roll = roll;
    }

    public Card getCard() {
        return card;
    }

    public int getRoll() {
        return roll;
    }

    public static Attack getAttackThatGoesFirst(Attack a1, Attack a2) {
        Card one = a1.getCard();
        Card two = a2.getCard();

        Card first;

        if (one.getHealth() > two.getHealth()) {
            // Card one goes first
            first = one;
        } else if (one.getHealth() < two.getHealth()) {
            // Card two goes first
            first = two;
        } else { // Tied
            if (one.getClass().getName().compareTo(two.getClass().getName()) == -1) {
                // Card one is alphabetically before card two so card one goes
                // first
                first = one;
            } else {
                // Card two goes first
                first = two;
            }
        }

        if (first == a1.getCard()) {
            return a1;
        } else {
            return a2;
        }
    }
}
