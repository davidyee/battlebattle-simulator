package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Survivalist extends Card {

    public class SurvivalistAction extends Action {

        public SurvivalistAction(boolean goingFirst, Card card, boolean useToken, int attack) {
            super(goingFirst, card, useToken, attack);
        }

        @Override
        protected void applySpecialAbilityBefore(Action opponent) {
            // Swap the battle dice with health
            int hp = getCard().getHealth();
            getCard().setHealth(getAttack());
            setAttack(hp);
        }
    }

    public Survivalist() {
        super(5, 1);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();

        // Play conservatively!
        // Don't use your token unless you are going to lose!
        // Take a slight risk if tied or lost and health is very low
        if (isTokenAvailableAndNotYetUsed) {
            boolean isGoingToLose = myRoll.getResult(theirRoll) == State.LOSE && getHealth() == 1;
            boolean isGoodRisk = (myRoll.getResult(theirRoll) == State.LOSE || myRoll.getResult(theirRoll) == State.TIE)
                    && getHealth() <= 2;
            if (isGoingToLose || isGoodRisk) {
                Action action = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack());
                action.setSpecialAbilityBefore(true);
                return action;
            }
        }

        Action action = new Action(myRoll);
        action.setBestAction(true);
        return action;
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format("%s is going to use a token in order to swap health with the rolled dice value (%d)!",
                    this.getClass().getSimpleName(), finalAction.getAttack());
        }

        return "";
    }

}
