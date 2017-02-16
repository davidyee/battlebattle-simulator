package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Trickster extends Card {

    public Trickster() {
        super(4, 0);
    }

    @Override
    public void applyResultOfRoll(int roll) {
        if (roll >= 0 && roll <= 3) {
            ++tokens;
        }
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) != State.WIN) {
            // Play conservatively!
            // Don't use your token unless you are going to lose!
            if (myRoll.getResult(theirRoll) == State.LOSE) {
                int roll = roll();
                Action action = new Action(myRoll.isGoingFirst(), this, true, roll);
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
            return String.format("%s is going to use a token in order to re-roll!", this.getClass().getSimpleName());
        }

        return "";
    }

}
