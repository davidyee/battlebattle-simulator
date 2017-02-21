package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Weenie extends Card {
    private int damageBonus = 1;
    private int numberOfExtraDice = 3;

    public Weenie() {
        super(2, 0);
    }

    public Weenie(int health, int tokens) {
        super(health, tokens);
    }

    public Weenie(Weenie copy) {
        super(copy);
        damageBonus = copy.damageBonus;
        numberOfExtraDice = copy.numberOfExtraDice;
    }

    @Override
    public int roll() {
        int roll = super.roll();

        if (roll == 4)
            roll = 3;
        else if (roll == 6)
            roll = 5;

        return roll;
    }

    @Override
    public void applyPassiveAfterEvaluation(Action myAction, Action opponentAction) {
        int opponentHealth = opponentAction.getCard().getHealth();
        int bonusDamage = 0;

        for (int i = 0; i < numberOfExtraDice; ++i) {
            int roll = super.roll();
            if (roll > opponentAction.getInitialAttack()) {
                bonusDamage += damageBonus * myAction.getDamageOpponent().getFactor();
            }
        }
        bonusDamage *= opponentAction.getDamageReceive().getFactor();

        LOGGER.debug(String.format(
                "%s rolled %d extra dice and beat the opponent's initial roll of %d to do a total extra damage of %d!",
                this.getClass().getSimpleName(), numberOfExtraDice, opponentAction.getInitialAttack(), bonusDamage));

        opponentHealth -= bonusDamage;
        opponentAction.getCard().setHealth(opponentHealth);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        Action action = myRoll.copy();
        action.setBestAction(true);

        return action;
    }

    protected int getDamageBonus() {
        return damageBonus;
    }

    protected void setDamageBonus(int damageBonus) {
        this.damageBonus = damageBonus;
    }

    protected int getNumberOfExtraDice() {
        return numberOfExtraDice;
    }

    protected void setNumberOfExtraDice(int numberOfExtraDice) {
        this.numberOfExtraDice = numberOfExtraDice;
    }

    @Override
    public Card copy() {
        return new Weenie(this);
    }
}
