package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class Trickster extends Card {
    private Integer virtualRoll = null;

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
        if (isTokenAvailableAndNotYetUsed) {
            // Play conservatively!
            // Don't use your token unless you are going to lose!
            
            // Roll and save the result until all evaluation is complete
            // This technique makes the simulation more predictable
            if(virtualRoll == null) virtualRoll = roll();
            
            Action defaultAction = new Action(myRoll);
            Action bonusAction = new Action(myRoll.isGoingFirst(), this, true, virtualRoll);

            return Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
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
    
    @Override
    public void applyPassiveAfterEvaluation(Action myAction, Action opponentAction) {
        virtualRoll = null; // reset the "virtual roll"
    }

}
