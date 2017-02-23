package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Assassin extends Card {

    public Assassin() {
        super(3, 1);
    }

    public Assassin(int health, int tokens) {
        super(health, tokens);
    }

    public Assassin(Assassin copy) {
        super(copy);
    }

    @Override
    public int roll() {
        int roll = super.roll();
        if (roll == 4 || roll == 5) {
            return 6;
        }

        return roll;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed) {
            Action defaultAction = new Action(myRoll);
            Action bonusAction = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() * 2);

            return Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
        }

        Action action = new Action(myRoll);
        action.setBestAction(true);
        return action;
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format("%s is going to use a token to double their roll!", this.getClass().getSimpleName(),
                    finalAction.getAttack() * 2);
        }

        return "";
    }

    @Override
    public Card copy() {
        return new Assassin(this);
    }

}
