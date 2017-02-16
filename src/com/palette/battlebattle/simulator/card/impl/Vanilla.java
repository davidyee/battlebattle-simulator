package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class Vanilla extends Card {
    private int bonusDamage = 1;

    public Vanilla() {
        super(5, 3);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();

        if (isTokenAvailableAndNotYetUsed) {
            Action defaultAction = myRoll.copy();
            Action bonusAction = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() + bonusDamage);
            return Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
        }

        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format("%s is going to use a token to add %d more to their roll!",
                    this.getClass().getSimpleName(), bonusDamage);
        }

        return "";
    }

}
