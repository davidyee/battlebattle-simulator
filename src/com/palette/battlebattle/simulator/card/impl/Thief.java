package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Action.State;
import com.palette.battlebattle.simulator.card.Card;

public class Thief extends Card {
    int addDamageNextRound = 0;

    public Thief() {
        super(4, 0);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed) {
            if (myRoll.getAttack() + 2 >= theirRoll.getAttack()) {
                Action action = new Action(this, true, myRoll.getAttack() + 2);
                return action;
            }
        }

        return myRoll;
    }

    @Override
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        myAction.setAttack(myAction.getAttack() + addDamageNextRound);
        addDamageNextRound = 0;
    }

    @Override
    public void applyPassiveAfterEvaluation(Action myAction, Action opponentAction) {
        // Apply this ability on receiving damage
        // Steals one token from the opponent if they have tokens available
        if (opponentAction.getResult(myAction) == State.WIN) {
            int opponentTokens = opponentAction.getCard().getTokens();
            if (opponentTokens > 0) {
                opponentAction.getCard().setTokens(opponentTokens - 1);
                ++tokens;
                LOGGER.debug(String.format("%s stole a token!", this.getClass().getSimpleName()));
            } else {
                // Add two damage next round
                addDamageNextRound = 2;
                LOGGER.debug(String.format(
                        "%s is going to add %d more to their roll next round because the opponent is out of tokens!",
                        this.getClass().getSimpleName(), addDamageNextRound));
            }
        }
    }

}
