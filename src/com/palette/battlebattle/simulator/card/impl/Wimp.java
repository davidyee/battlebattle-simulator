package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class Wimp extends Card {

    public Wimp() {
        super(3, 0);
    }

    @Override
    public void applyResultOfRoll(int roll) {
        if (roll >= 0 && roll <= 3) {
            ++tokens;
        }
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        boolean isBestActionPerformedAlready = myRoll.isBestAction();
        if (!isBestActionPerformedAlready) {
            if (theirRoll.getCard().getHealth() > getHealth()) {
                Action action = new Action(this, false, myRoll.getAttack() + 3);
                action.setBestAction(true);
                LOGGER.debug(String.format("%s is going to increase the roll strength by 3!",
                        this.getClass().getSimpleName()));
                return action;
            }
        }

        return myRoll;
    }

}
