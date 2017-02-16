package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class Wimp extends Card {

    public Wimp() {
        super(3, 0);
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
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        if (getHealth() < opponentAction.getCard().getHealth()) {
            myAction.setAttack(myAction.getAttack() + 3);
        }
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (getHealth() < finalOpponentAction.getCard().getHealth()) {
            return String.format("%s increases their roll strength by 3 due to a lower health!",
                    this.getClass().getSimpleName());
        }

        return "";
    }

}
