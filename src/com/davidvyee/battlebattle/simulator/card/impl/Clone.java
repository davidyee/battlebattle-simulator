package com.davidvyee.battlebattle.simulator.card.impl;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.davidvyee.battlebattle.simulator.card.Action;
import com.davidvyee.battlebattle.simulator.card.Card;

public class Clone extends Card {
    private Set<Class<? extends Card>> cards = Card.getCardClasses();

    public class CloneAction extends Action {

        public CloneAction(Action copy) {
            super(copy);
        }

        public CloneAction(boolean goingFirst, Card card, boolean useToken, int attack) {
            super(goingFirst, card, useToken, attack);
        }

        @Override
        protected void applySpecialAbilityBefore(Action opponent) {
            Set<Class<? extends Card>> adjustedCards = cards.stream()
                    .filter(c -> !c.equals(Clone.class) && !c.equals(opponent.getCard().getClass()))
                    .collect(Collectors.toSet());

            if (adjustedCards.size() == 0) {
                LOGGER.warn(String.format("%s could not switch its card because it could not find a different card!",
                        Clone.class.getSimpleName()));
                return;
            }

            int index = new Random().nextInt(cards.size());
            Iterator<Class<? extends Card>> iter = cards.iterator();
            for (int i = 0; i < index; ++i) {
                iter.next();
            }
            Class<? extends Card> newCardClass = iter.next();

            try {
                Card newCard = newCardClass.newInstance();
                LOGGER.debug(String.format("%s is going to transform into %s", Clone.class.getSimpleName(),
                        newCard.getClass().getSimpleName()));
                setMorphCard(newCard);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(String.format("%s encountered a problem when attempting to instantiate a new card!",
                        Clone.class.getSimpleName()));
                e.printStackTrace();
                return;
            }
        }

        @Override
        public Action copy() {
            return new CloneAction(this);
        }
    }

    public Clone() {
        super(3, 1);
    }

    public Clone(int health, int tokens) {
        super(health, tokens);
    }

    public Clone(Clone copy) {
        super(copy);
    }

    @Override
    public Action getBestAction(Action myRoll, Action theirRoll) {
        if (myRoll.isBestAction())
            return myRoll;

        Action action = myRoll.copy();

        boolean isTokenAvailableAndNotYetUsed = myRoll.isTokenAvailable() && !myRoll.isUseToken();
        if (isTokenAvailableAndNotYetUsed) {
            if (getHealth() == 1) {
                action = new CloneAction(myRoll);
                action.setSpecialAbilityBefore(true);
            }
        }

        action.setBestAction(true);

        return action;
    }

    @Override
    public Card copy() {
        return new Clone(this);
    }

}
