package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class TheGeneral extends Card {
    private static final int ROLL_ADJUSTMENT = 1;

    public class TheGeneralAction extends Action {
        public TheGeneralAction(Action copy) {
            super(copy);
        }
        
        public TheGeneralAction(boolean goingFirst, Card card, boolean useToken, int attack) {
            super(goingFirst, card, useToken, attack);
        }

        @Override
        protected void applySpecialAbilityBefore(Action opponent) {
            if (opponent.getAttack() == getAttack()) {
                setAttack(Integer.MAX_VALUE); // force a win
            }
        }

        @Override
        public State getResult(Action opponent) {
            State s = super.getResult(opponent);
            if (s == State.TIE)
                return State.WIN;
            return s;
        }
        
        @Override
        public Action copy() {
            return new TheGeneralAction(this);
        }
    }

    public TheGeneral() {
        super(4, 3, true);
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
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        Action action = new TheGeneralAction(myRoll);

        if (isTokenAvailableAndNotYetUsed) {
            Action defaultAction = new TheGeneralAction(myRoll);
            Action bonusAction = new TheGeneralAction(myRoll.isGoingFirst(), this, true,
                    myRoll.getAttack() + ROLL_ADJUSTMENT);

            action = Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
        }

        action.setSpecialAbilityBefore(true);

        return action;
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format("%s is going to use a token to reduce the opponent's roll by %d!",
                    this.getClass().getSimpleName(), ROLL_ADJUSTMENT);
        }

        return "";
    }

}
