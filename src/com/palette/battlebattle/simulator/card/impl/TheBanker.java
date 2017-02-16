package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class TheBanker extends Card {
    private static final int ROLL_BONUS = 2;

    public TheBanker() {
        super(4, 0);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isTokenAvailable() && !myRoll.isUseToken()) {
            if (myRoll.getAttack() + ROLL_BONUS >= theirRoll.getAttack()) {
                Action action = new Action(this, true, myRoll.getAttack() + ROLL_BONUS);
                return action;
            }
        }

        return myRoll;
    }

    @Override
    public void applyPassiveAfterLostHealth(Action attacker, int deltaHealth) {
        if (deltaHealth < 0) { // if lost health then add that amount to tokens
            setTokens(getTokens() + Math.abs(deltaHealth));
        }
    }

}
