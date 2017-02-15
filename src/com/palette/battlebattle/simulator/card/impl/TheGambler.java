package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class TheGambler extends Card {
    private static final int DAMAGE_FACTOR = 2;
    private boolean multiplyDamage = false;

    private class TheGamblerAction extends Action {

        public TheGamblerAction(Card card, boolean useToken, int attack) {
            super(card, useToken, attack);
        }

        @Override
        protected void applySpecialAbilityBefore(Action opponent) {
            getDamageReceive().setFactor(0);
        }

        @Override
        protected void applySpecialAbilityAfter(Action opponent) {
            multiplyDamage = true;
        }

    }

    public TheGambler() {
        super(5, 3);
    }
    
    @Override
    public int roll() {
        int roll = super.roll();
        if(roll == 3 || roll == 4) {
            return super.roll();
        }
        
        return roll;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) == State.LOSE) {
            Action action = new TheGamblerAction(this, true, myRoll.getAttack());
            action.setSpecialAbilityBefore(true);
            action.setSpecialAbilityAfter(true);
            LOGGER.debug(String.format(
                    "%s is going to become immune for this round and increase everyone's health damage by %d next round!",
                    this.getClass().getSimpleName(), DAMAGE_FACTOR));
            return action;
        }

        return myRoll;
    }

    @Override
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        if (multiplyDamage) {
            myAction.getDamageOpponent().setFactor(DAMAGE_FACTOR);
            opponentAction.getDamageOpponent().setFactor(DAMAGE_FACTOR);
            multiplyDamage = false;
        }
    }

}
