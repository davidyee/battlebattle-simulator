package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class Weenie extends Card {
    private static final int DAMAGE_BONUS = 1;
    private static final int NUMBER_OF_EXTRA_DICE = 3;

    public Weenie() {
        super(2, 0);
    }

    @Override
    public int roll() {
        int roll = super.roll();

        if (roll == 4)
            return roll = 3;
        else if (roll == 6)
            return roll = 5;
        else
            return roll;
    }

    @Override
    public void applyPassiveAfterEvaluation(Action myAction, Action opponentAction) {
        int opponentHealth = opponentAction.getCard().getHealth();
        int bonusDamage = 0;

        for (int i = 0; i < NUMBER_OF_EXTRA_DICE; ++i) {
            int roll = roll();
            if (roll > opponentAction.getInitialAttack()) {
                bonusDamage += DAMAGE_BONUS;
            }
        }

        LOGGER.debug(String.format(
                "%s rolled %d extra dice and beat the opponent's initial roll of %d to do a total extra damage of %d!",
                this.getClass().getSimpleName(), NUMBER_OF_EXTRA_DICE, opponentAction.getInitialAttack(), bonusDamage));

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

}
