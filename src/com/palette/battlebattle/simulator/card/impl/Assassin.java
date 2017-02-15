package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Assassin extends Card {

    public Assassin() {
        super(3, 1);
    }

    @Override
    public int roll() {
        int roll = super.roll();
        if (roll == 4 || roll == 5) {
            return 6;
        }

        return roll;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) == State.LOSE) {
            if (myRoll.getAttack() * 2 >= theirRoll.getAttack()) {
                Action action = new Action(this, true, myRoll.getAttack() * 2);
                LOGGER.debug(String.format("%s is going to use a token to double their roll!",
                        this.getClass().getSimpleName(), myRoll.getAttack() * 2));
                return action;
            }
        }

        return myRoll;
    }

}
