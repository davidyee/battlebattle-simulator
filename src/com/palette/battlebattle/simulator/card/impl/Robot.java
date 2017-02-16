package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Robot extends Card {
    private int bonusDamage = 2;

    public Robot() {
        super(6, 4);
    }

    @Override
    public int roll() {
        return 3;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) != State.WIN) {
            if (myRoll.getAttack() + bonusDamage >= theirRoll.getAttack() && tokens > 0) {
                Action action = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() + bonusDamage);
                return action;
            }
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
