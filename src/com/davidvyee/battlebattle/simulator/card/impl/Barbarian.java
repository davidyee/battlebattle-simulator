package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Barbarian extends Card {
    private int damageBonus = 1;
    private int damageReceiveFactor = 2;
    private int numberOfExtraDice = 2;

    public Barbarian() {
        super(6, 0);
    }

    public Barbarian(int health, int tokens) {
        super(health, tokens);
    }

    public Barbarian(Barbarian copy) {
        super(copy);
        damageBonus = copy.damageBonus;
        damageReceiveFactor = copy.damageReceiveFactor;
        numberOfExtraDice = copy.numberOfExtraDice;
    }

    @Override
    public int roll() {
        int roll = super.roll();

        if (roll == 1 || roll == 2 || roll == 3)
            return 4;

        return roll;
    }

    @Override
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        myAction.getDamageReceive().setFactor(damageReceiveFactor);
    }

    @Override
    public void applyPassiveAfterEvaluation(Action myAction, Action opponentAction) {
        int opponentHealth = opponentAction.getCard().getHealth();
        int bonusDamage = 0;

        for (int i = 0; i < numberOfExtraDice; ++i) {
            int roll = super.roll();
            if (roll > opponentAction.getInitialAttack()) {
                bonusDamage += damageBonus;
            }
        }

        bonusDamage *= opponentAction.getDamageReceive().getFactor();

        if (bonusDamage > 0) {
            LOGGER.debug(String.format(
                    "%s rolled %d extra dice and beat the opponent's initial roll of %d to do a total extra damage of %d!",
                    this.getClass().getSimpleName(), numberOfExtraDice, opponentAction.getInitialAttack(),
                    bonusDamage));
        }

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

    protected int getDamageReceiveFactor() {
        return damageReceiveFactor;
    }

    protected void setDamageReceiveFactor(int damageReceiveFactor) {
        this.damageReceiveFactor = damageReceiveFactor;
    }

    protected int getNumberOfExtraDice() {
        return numberOfExtraDice;
    }

    protected void setNumberOfExtraDice(int numberOfExtraDice) {
        this.numberOfExtraDice = numberOfExtraDice;
    }

    @Override
    public Card copy() {
        return new Barbarian(this);
    }

}
