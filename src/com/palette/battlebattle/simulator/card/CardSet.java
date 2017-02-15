package com.palette.battlebattle.simulator.card;

/**
 * The tuple of cards that need to be tested against each other.
 *
 * @author David Yee
 */
public class CardSet {
    private final Class<? extends Card> one;
    private final Class<? extends Card> two;

    public CardSet(Class<? extends Card> one, Class<? extends Card> two) {
        if (one == null || two == null) {
            throw new IllegalArgumentException("Both classes in the card set must be non-null!");
        }

        this.one = one;
        this.two = two;
    }

    public Class<? extends Card> getOne() {
        return one;
    }

    public Class<? extends Card> getTwo() {
        return two;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * (1 + one.hashCode() + two.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CardSet other = (CardSet) obj;

        if ((one.equals(other.one) && two.equals(other.two)) || (two.equals(other.one) && one.equals(other.two)))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "CardSet [one=" + one.getSimpleName() + ", two=" + two.getSimpleName() + "]";
    }

}