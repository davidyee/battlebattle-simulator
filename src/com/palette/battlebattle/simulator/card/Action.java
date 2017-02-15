package com.palette.battlebattle.simulator.card;

/**
 * Represents an action taken by a given {@link Card}, including state
 * information such as attack strength and if a token was used or not.
 *
 * @author David Yee
 */
public class Action {
    private final Card card;
    private final boolean useToken;
    private int attack;
    private boolean useSpecialAbilityBefore;
    private boolean useSpecialAbilityAfter;

    public enum State {
        WIN, LOSE, TIE;
    }

    public Action(Card card, boolean useToken, int attack) {
        super();
        this.card = card;
        this.useToken = useToken;
        this.attack = attack;
    }

    public Action(Attack attack) {
        super();
        this.card = attack.getCard();
        this.useToken = false;
        this.attack = attack.getRoll();
    }

    public Card getCard() {
        return card;
    }

    public boolean isUseToken() {
        return useToken;
    }

    public boolean isTokenAvailable() {
        return card.tokens > 0;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getAttack() {
        return attack;
    }

    public void setSpecialAbilityBefore(boolean enabled) {
        this.useSpecialAbilityBefore = enabled;
    }
    
    public void setSpecialAbilityAfter(boolean enabled) {
        this.useSpecialAbilityAfter = enabled;
    }

    /**
     * Runs before evaluating the attack against the other card.
     * 
     * @param against
     *            The other card.
     */
    protected void applySpecialAbilityBefore(Card against) {
    }

    /**
     * Runs after evaluating the attack against the other card.
     * 
     * @param against
     */
    protected void applySpecialAbilityAfter(Card against) {
    }

    /**
     * Determine if this action will win, lose, or tie against another action.
     * 
     * @param opponent
     *            The opponent's action.
     * @return The result as a win, lose, or tie.
     */
    public State getResult(Action opponent) {
        if (attack > opponent.attack) {
            return State.WIN;
        } else if (attack < opponent.attack) {
            return State.LOSE;
        } else {
            return State.TIE;
        }
    }

    /**
     * Applies the actions to the card states.
     * 
     * @param one
     *            The best action of the first card.
     * @param two
     *            The best action of the second card.
     */
    public static void evaluateActions(Action one, Action two) {
        // Before Hooks
        if (one.useSpecialAbilityBefore) {
            one.applySpecialAbilityBefore(two.getCard());
        }
        
        if (two.useSpecialAbilityBefore) {
            two.applySpecialAbilityBefore(one.getCard());
        }
        
        // Apply Attack
        if (one.getAttack() > two.getAttack()) {
            --two.card.health;
        } else if (one.getAttack() < two.getAttack()) {
            --one.card.health;
        }

        // Apply Token Loss
        if (one.useToken) {
            --one.card.tokens;
        }

        if (two.useToken) {
            --two.card.tokens;
        }

        // After Hooks
        if (one.useSpecialAbilityAfter) {
            one.applySpecialAbilityAfter(two.getCard());
        }

        if (two.useSpecialAbilityAfter) {
            two.applySpecialAbilityAfter(one.getCard());
        }
    }

}
