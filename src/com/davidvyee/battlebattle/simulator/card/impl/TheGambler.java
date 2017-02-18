package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;
import com.davidvyee.battlebattle.simulator.card.Action.State;

public class TheGambler extends Card {
    private static final int DAMAGE_FACTOR = 2;
    private boolean multiplyDamage = false;

    private class TheGamblerAction extends Action {

        public TheGamblerAction(Action copy) {
            super(copy);
        }

        public TheGamblerAction(boolean goingFirst, Card card, boolean useToken, int attack) {
            super(goingFirst, card, useToken, attack);
            setSpecialAbilityBefore(useToken);
            setSpecialAbilityAfter(useToken);
        }

        @Override
        protected void applySpecialAbilityBefore(Action opponent) {
            getDamageReceive().setFactor(0);
        }

        @Override
        protected void applySpecialAbilityAfter(Action opponent) {
            multiplyDamage = true;
        }

        @Override
        public Action copy() {
            return new TheGamblerAction(this);
        }
    }

    public TheGambler() {
        super(5, 3);
    }

    public TheGambler(int health, int tokens) {
        super(health, tokens);
    }

    public TheGambler(TheGambler copy) {
        super(copy);
    }

    @Override
    public int roll() {
        int roll = super.roll();
        if (roll == 3 || roll == 4) {
            return roll();
        }

        return roll;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed && myRoll.getResult(theirRoll) == State.LOSE) {
            Action defaultAction = new TheGamblerAction(myRoll);
            Action bonusAction = new TheGamblerAction(myRoll.isGoingFirst(), this, true, myRoll.getAttack());

            return Action.getBestActionBetweenDefaultActionAndBonusAction(theirRoll, defaultAction, bonusAction);
        }

        Action action = myRoll.copy();
        action.setBestAction(true);
        return action;
    }

    @Override
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        if (multiplyDamage) {
            myAction.getDamageOpponent().setFactor(DAMAGE_FACTOR);
            opponentAction.getDamageOpponent().setFactor(DAMAGE_FACTOR);
            multiplyDamage = false;
        }
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format(
                    "%s is going to become immune for this round and increase everyone's health damage by %d next round!",
                    this.getClass().getSimpleName(), DAMAGE_FACTOR);
        }

        return "";
    }

    @Override
    public Card copy() {
        return new TheGambler(this);
    }

}
