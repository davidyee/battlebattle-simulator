package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Sniper extends Card {

    public Sniper() {
        super(4, 0);
    }

    public Sniper(int health, int tokens) {
        super(health, tokens);
    }

    public Sniper(Sniper copy) {
        super(copy);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

    @Override
    public Card copy() {
        return new Sniper(this);
    }

}
