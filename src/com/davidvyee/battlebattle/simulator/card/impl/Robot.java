package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;
import com.davidvyee.battlebattle.simulator.card.Action.State;

public class Robot extends Card {
    private int bonusDamage = 2;

    public Robot() {
        super(6, 4);
    }

    public Robot(int health, int tokens) {
        super(health, tokens);
    }

    public Robot(Robot copy) {
        super(copy);
    }

    @Override
    public int roll() {
        return 3;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) != State.WIN) {
            Action defaultAction = new Action(myRoll);
            Action bonusAction = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() + bonusDamage);

            return Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
        }

        Action action = myRoll.copy();
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

    @Override
    public Card copy() {
        return new Robot(this);
    }

}
