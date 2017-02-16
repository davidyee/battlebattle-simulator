package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class Sniper extends Card {

    public Sniper() {
        super(4, 0);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

}
