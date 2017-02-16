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
        if (myRoll.isBestAction())
            return myRoll;
        
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed) {
            Action defaultAction = new Action(myRoll);
            Action bonusAction = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() + ROLL_BONUS);

            return Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
        }

        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

    @Override
    public void applyPassiveAfterLostHealth(Action attacker, int deltaHealth) {
        if (deltaHealth < 0) { // if lost health then add that amount to tokens
            setTokens(getTokens() + Math.abs(deltaHealth));
        }
    }

}
