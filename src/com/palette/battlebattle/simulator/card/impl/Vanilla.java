package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Vanilla extends Card {
    private int bonusDamage = 1;

    public Vanilla() {
        super(5, 3);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();

        if (isTokenAvailableAndNotYetUsed) {
            Action actionWithoutBonus = new Action(myRoll);
            actionWithoutBonus.setBestAction(true); // fake best action

            Action actionWithBonus = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() + bonusDamage);
            actionWithBonus.setBestAction(true); // fake best action

            Action theirActionToOurActionWithoutBonus = theirRoll.getCard().getBestAction(theirRoll,
                    actionWithoutBonus);
            Action theirActionToOurActionWithBonus = theirRoll.getCard().getBestAction(theirRoll, actionWithBonus);

            Action result;

            boolean isWinnableWithoutBonus = theirActionToOurActionWithoutBonus
                    .getResult(actionWithoutBonus) == State.LOSE;
            boolean isWinnableWithBonus = theirActionToOurActionWithBonus.getResult(actionWithBonus) == State.LOSE
                    || theirActionToOurActionWithBonus.getResult(actionWithBonus) == State.TIE;
            boolean willLoseGoingSecond = myRoll.isGoingSecond() && isWinnableWithBonus
                    && theirActionToOurActionWithBonus.getResult(actionWithoutBonus) == State.WIN;
            if (isWinnableWithoutBonus && !willLoseGoingSecond) {
                // do nothing because we'll win anyways
                result = actionWithoutBonus;
            } else if (isWinnableWithBonus) {
                result = actionWithBonus;
            } else {
                result = actionWithoutBonus;
            }

            return result;
        }

        Action action = new Action(myRoll);
        action.setBestAction(true);
        return action;
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format("%s is going to use a token to add %d more to their roll!",
                    this.getClass().getSimpleName(), bonusDamage);
        }

        return "";
    }

}
