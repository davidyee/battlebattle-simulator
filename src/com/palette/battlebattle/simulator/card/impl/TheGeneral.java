package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class TheGeneral extends Card {
    private static final int ROLL_ADJUSTMENT = 1;

    private class TheGeneralAction extends Action {
        public TheGeneralAction(Card card, boolean useToken, int attack) {
            super(card, useToken, attack);
        }

        @Override
        protected void applySpecialAbilityBefore(Action opponent) {
            if (opponent.getAttack() == getAttack()) {
                setAttack(Integer.MAX_VALUE); // force a win
            }
        }
    }

    public TheGeneral() {
        super(4, 3);
    }

    @Override
    public void applySecondCardAdvantageBeforeEvaluation(Action myAction, Action opponentAction) {
        // This clause usually only applies when dualing another card that
        // forces a win (e.g. against another ruler)
        if (myAction.isUseToken() && opponentAction.getAttack() == Integer.MAX_VALUE) {
            opponentAction.setAttack(opponentAction.getInitialAttack());
        }
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        Action action = myRoll;

        if (isTokenAvailableAndNotYetUsed && myRoll.getAttack() == theirRoll.getAttack() - ROLL_ADJUSTMENT) {
            action = new TheGeneralAction(this, true, myRoll.getAttack());
            LOGGER.debug(String.format("%s is going to reduce the opponent's token by one to attempt a tie!",
                    this.getClass().getSimpleName()));
        }

        action.setSpecialAbilityBefore(true);

        return action;
    }

}
