package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Survivalist extends Card {

    public class SurvivalistAction extends Action {

        public SurvivalistAction(Card card, boolean useToken, int attack) {
            super(card, useToken, attack);
        }

        @Override
        protected void applySpecialAbilityBefore(Card against) {
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
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();

        // Play conservatively!
        // Don't use your token unless you are going to lose!
        // Take a slight risk if tied or lost and health is very low
        if (isTokenAvailableAndNotYetUsed) {
            boolean isGoingToLose = myRoll.getResult(theirRoll) == State.LOSE && getHealth() == 1;
            boolean isGoodRisk = (myRoll.getResult(theirRoll) == State.LOSE || myRoll.getResult(theirRoll) == State.TIE)
                    && getHealth() <= 2;
            if (isGoingToLose || isGoodRisk) {
                Action action = new Action(this, true, myRoll.getAttack());
                action.setSpecialAbilityBefore(true);
                LOGGER.debug(String.format(
                        "%s is going to use a token in order to swap health with the rolled dice value (%d)!",
                        this.getClass().getSimpleName(), myRoll.getAttack()));
                return action;
            }
        }

        return myRoll;
    }

}
