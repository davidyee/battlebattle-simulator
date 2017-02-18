package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Thief extends Card {
    int addDamageNextRound = 0;

    public Thief() {
        super(4, 0);
    }

    public Thief(int health, int tokens) {
        super(health, tokens);
    }

    public Thief(Thief copy) {
        super(copy);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed) {
            Action defaultAction = new Action(myRoll);
            Action bonusAction = new Action(myRoll.isGoingFirst(), this, true, myRoll.getAttack() + 2);

            return Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
        }

        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

    @Override
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        if (addDamageNextRound > 0) {
            LOGGER.debug(String.format("%s added %d to their original roll of %d!", this.getClass().getSimpleName(),
                    addDamageNextRound, myAction.getAttack()));
        }

        myAction.setAttack(myAction.getAttack() + addDamageNextRound);
        addDamageNextRound = 0;
    }

    @Override
    public void applyPassiveAfterLostHealth(Action attacker, int deltaHealth) {
        if (deltaHealth < 0) {
            int attackerTokens = attacker.getCard().getTokens();
            if (attackerTokens > 0) {
                attacker.getCard().setTokens(attackerTokens - 1);
                setTokens(getTokens() + 1);
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

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        return "";
    }

    @Override
    public Card copy() {
        return new Thief(this);
    }

}
