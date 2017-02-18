package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;
import com.davidvyee.battlebattle.simulator.card.Action.State;

public class Wizard extends Card {
    private int bonusDamage = 1;

    public class WizardAction extends Action {
        boolean isBonusDamage = false;

        public WizardAction(Action copy) {
            super(copy);
        }

        public WizardAction(boolean goingFirst, Card card, boolean useToken, int attack) {
            super(goingFirst, card, useToken, attack);
        }

        @Override
        protected void applySpecialAbilityBefore(Action opponent) {
            if (isBonusDamage) {
                int opponentHealth = opponent.getCard().getHealth();
                opponent.getCard().setHealth(opponentHealth - bonusDamage);
            } else { // does no damage
                getDamageOpponent().setFactor(0);
            }
        }

        @Override
        public Action copy() {
            return new WizardAction(this);
        }
    }

    public Wizard() {
        super(2, 5);
    }

    public Wizard(int health, int tokens) {
        super(health, tokens);
    }

    public Wizard(Wizard copy) {
        super(copy);
    }

    /**
     * Determines if the risk is worth taking. An acceptable risk includes a 50%
     * or better potential for beating the opponent's roll or if the player is
     * about to die.
     * 
     * @param myRoll
     * @param theirRoll
     * @return <b>true</b> if the wizard should use a token to attempt to do
     *         more damage; <b>false</b> otherwise.
     */
    private boolean isRiskWorthIt(Action myRoll, Action theirRoll) {
        // assumes 6 sided dice
        int difference = 6 - theirRoll.getAttack();
        int adjustedDifference = difference;
        if (myRoll.getAttack() > difference)
            --adjustedDifference;
        if (adjustedDifference >= 3) { // 50% potential
            return true;
        } else {
            boolean mayLose = myRoll.getResult(theirRoll) == State.LOSE || theirRoll.getResult(myRoll) == State.WIN;
            if (mayLose) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        Action action = myRoll.copy();

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();

        // Only risk it if the opponent rolled a one or two
        if (isTokenAvailableAndNotYetUsed && isRiskWorthIt(myRoll, theirRoll)) {
            // Roll and save the result until all evaluation is complete
            // This technique makes the simulation more predictable
            int virtualRoll = roll();

            action = new WizardAction(myRoll.isGoingFirst(), this, true, myRoll.getAttack());
            action.setSpecialAbilityBefore(true);

            if (virtualRoll % 2 == 0) { // does not damage on
                ((WizardAction) action).isBonusDamage = false;
            } else {
                ((WizardAction) action).isBonusDamage = true;
            }
        }

        action.setBestAction(true);
        return action;
    }

    @Override
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        if (finalAction.isUseToken()) {
            return String.format("%s is going to use a token to roll again to attempt to do an extra damage!",
                    this.getClass().getSimpleName());
        }

        return "";
    }

    @Override
    public Card copy() {
        return new Wizard(this);
    }

}
