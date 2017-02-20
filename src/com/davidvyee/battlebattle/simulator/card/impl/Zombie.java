package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Zombie extends Card {

    public class ZombieAction extends Action {
        public ZombieAction(Action copy) {
            super(copy);
        }

        public ZombieAction(boolean goingFirst, Card card, boolean useToken, int attack) {
            super(goingFirst, card, useToken, attack);
        }

        @Override
        public State getResult(Action opponent) {
            if (getAttack() == 1) {
                return State.TIE;
            } else {
                return super.getResult(opponent);
            }
        }

        @Override
        public Action copy() {
            return new ZombieAction(this);
        }
    }

    public Zombie() {
        super(4, 0);
    }

    public Zombie(int health, int tokens) {
        super(health, tokens);
    }

    public Zombie(Zombie copy) {
        super(copy);
    }

    @Override
    public int roll() {
        int roll = super.roll();
        if (roll == 6)
            return 1;
        return roll;
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        Action action = new ZombieAction(myRoll.isGoingFirst(), this, false, myRoll.getAttack());
        action.setBestAction(true);

        if (myRoll.getAttack() == 1) {
            action.getDamageReceive().setFactor(1);
            action.getDamageReceive().setDelta(0);
        } else {
            action.getDamageReceive().setFactor(0);
            action.getDamageReceive().setDelta(0);
        }

        return action;
    }

    @Override
    public Card copy() {
        return new Zombie(this);
    }

}
