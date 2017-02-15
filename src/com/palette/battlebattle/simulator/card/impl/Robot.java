package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Robot extends Card {
    int bonusDamage = 2;

    public Robot() {
        super(6, 4);
    }

    @Override
    public int roll() {
        return 3;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) != State.WIN) {
            if (myRoll.getAttack() + bonusDamage >= theirRoll.getAttack() && tokens > 0) {
                Action action = new Action(this, true, myRoll.getAttack() + bonusDamage);
                LOGGER.debug(String.format("%s is going to use a token to add %d more to their roll!",
                        this.getClass().getSimpleName(), bonusDamage));
                return action;
            }
        }

        return myRoll;
    }

}
