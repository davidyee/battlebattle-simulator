package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class TheRuler extends Card {

    public TheRuler() {
        super(4, 2);
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

        // Play conservatively!
        // Don't use your token unless you are going to lose!
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) == State.LOSE) {
            Action action = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack());
            action.setAttack(Integer.MAX_VALUE);
            return action;
        }

        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format("%s is going to use a token in order to win the round!",
                    this.getClass().getSimpleName());
        }

        return "";
    }
    
}
