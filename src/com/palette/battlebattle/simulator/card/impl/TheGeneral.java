package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class TheGeneral extends Card {
    private static final int ROLL_ADJUSTMENT = 1;

    private class TheGeneralAction extends Action {
        public TheGeneralAction(boolean goingFirst, Card card, boolean useToken, int attack) {
            super(goingFirst, card, useToken, attack);
        }

        public TheGeneralAction(Action copy) {
            super(copy);
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
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        Action action = new Action(myRoll);

        if (isTokenAvailableAndNotYetUsed) {
            Action actionWithoutBonus = new TheGeneralAction(myRoll);
            actionWithoutBonus.setBestAction(true); // fake best action

            Action actionWithBonus = new TheGeneralAction(myRoll.isGoingFirst(), this, true,
                    myRoll.getAttack() + ROLL_ADJUSTMENT);
            actionWithBonus.setBestAction(true); // fake best action

            Action theirActionToOurActionWithoutBonus = theirRoll.getCard().getBestAction(theirRoll,
                    actionWithoutBonus);
            Action theirActionToOurActionWithBonus = theirRoll.getCard().getBestAction(theirRoll, actionWithBonus);

            boolean isWinnableWithoutBonus = theirActionToOurActionWithoutBonus
                    .getResult(actionWithoutBonus) == State.LOSE
                    || theirActionToOurActionWithoutBonus.getResult(actionWithoutBonus) == State.TIE;
            boolean isWinnableWithBonus = theirActionToOurActionWithBonus.getResult(actionWithBonus) == State.LOSE
                    || theirActionToOurActionWithBonus.getResult(actionWithBonus) == State.TIE;
            boolean willLoseGoingSecond = myRoll.isGoingSecond() && isWinnableWithBonus
                    && theirActionToOurActionWithBonus.getResult(actionWithoutBonus) == State.WIN;

            if (isWinnableWithoutBonus && !willLoseGoingSecond) {
                // do nothing because we'll win anyways
                action = actionWithoutBonus;
            } else if (isWinnableWithBonus) {
                action = actionWithBonus;
            } else {
                action = actionWithoutBonus;
            }
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
