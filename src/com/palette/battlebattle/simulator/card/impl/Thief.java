package com.palette.battlebattle.simulator.card.impl;

import com.palette.battlebattle.simulator.card.Action;
import com.palette.battlebattle.simulator.card.Card;

public class Thief extends Card {
    int addDamageNextRound = 0;

    public Thief() {
        super(4, 0);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed) {
            if (myRoll.getAttack() + 2 >= theirRoll.getAttack()) {
                Action action = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() + 2);
                return action;
            }
        }

        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

    @Override
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        myAction.setAttack(myAction.getAttack() + addDamageNextRound);
        addDamageNextRound = 0;
    }

    @Override
    public void applyPassiveAfterLostHealth(Action attacker, int deltaHealth) {
        if (deltaHealth < 0) {
            int attackerTokens = attacker.getCard().getTokens();
            if (attackerTokens > 0) {
                attacker.getCard().setTokens(attackerTokens - 1);
                ++tokens;
            } else {
                // Add two damage next round
                addDamageNextRound = 2;
            }
        }
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        int attackerTokens = finalOpponentAction.getCard().getTokens();
        if (attackerTokens > 0) {
            return String.format("%s stole a token!", this.getClass().getSimpleName());
        } else {
            return String.format(
                    "%s is going to add %d more to their roll next round because the opponent is out of tokens!",
                    this.getClass().getSimpleName(), addDamageNextRound);
        }
    }

}
