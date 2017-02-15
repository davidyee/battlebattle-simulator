package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class TheRuler extends Card {

    public TheRuler() {
        super(4, 2);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();

        // Play conservatively!
        // Don't use your token unless you are going to lose!
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) == State.LOSE) {
            Action action = new Action(this, true, Integer.MAX_VALUE);
            LOGGER.debug(String.format("%s is going to use a token in order to win the round!",
                    this.getClass().getSimpleName()));
            return action;
        }

        return myRoll;
    }

}
