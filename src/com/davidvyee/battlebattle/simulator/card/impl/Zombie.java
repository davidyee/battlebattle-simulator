package com.davidvyee.battlebattle.simulator.card.impl;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Zombie extends Card {

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

        Action action = new Action(myRoll.isGoingFirst(), this, false, myRoll.getAttack());
        action.setBestAction(true);

        if (myRoll.getAttack() == 1) {
            action.getDamageReceive().setDelta(0);
            action.getDamageReceive().setFactor(0);
        }

        return action;
    }

    @Override
    public Card copy() {
        return new Zombie(this);
    }

}
