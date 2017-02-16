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
    private final boolean goingFirst;
    private int attack;
    private final int initialAttack; // keeps track of the initial attack value
    private boolean useSpecialAbilityBefore;
    private boolean useSpecialAbilityAfter;

    // damage to inflict on opponent when attacking
    private Damage damageOpponent = new Damage(1, -1);
    // damage to receive when attacked
    private Damage damageReceive = new Damage(1, 0);

    public class Damage {
        private int factor; // multiplicative effect
        private int delta; // additive effect

        public Damage(int factor, int delta) {
            super();
            this.factor = factor;
            this.delta = delta;
        }

        public int getFactor() {
            return factor;
        }

        public void setFactor(int factor) {
            this.factor = factor;
        }

        public int getDelta() {
            return delta;
        }

        public void setDelta(int delta) {
            this.delta = delta;
        }

    }

    // Indicates if already the best action for the current round
    private boolean bestAction;

    public enum State {
        WIN, LOSE, TIE, UNKNOWN;
    }

    public Action(Action copy) {
        this(copy.isGoingFirst(), copy.getCard(), copy.isUseToken(), copy.getAttack());
    }

    public Action(boolean goingFirst, Card card, boolean useToken, int attack) {
        super();
        this.goingFirst = goingFirst;
        this.card = card;
        this.useToken = useToken;
        this.attack = attack;
        this.initialAttack = attack;

        if (useToken)
            bestAction = true;
    }

    public Action(boolean goingFirst, Attack attack) {
        super();
        this.goingFirst = goingFirst;
        this.card = attack.getCard();
        this.useToken = false;
        this.attack = attack.getRoll();
        this.initialAttack = attack.getRoll();
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

    public boolean isGoingFirst() {
        return goingFirst;
    }

    public boolean isGoingSecond() {
        return !goingFirst;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getAttack() {
        return attack;
    }

    public int getInitialAttack() {
        return initialAttack;
    }

    public void setBestAction(boolean bestAction) {
        this.bestAction = bestAction;
    }

    public boolean isBestAction() {
        return bestAction;
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
     * @param opponent
     *            The opponent's card.
     */
    protected void applySpecialAbilityBefore(Action opponent) {
    }

    /**
     * Runs after evaluating the attack against the other card.
     * 
     * @param opponent
     *            The opponent's card.
     */
    protected void applySpecialAbilityAfter(Action opponent) {
    }

    public Damage getDamageOpponent() {
        return damageOpponent;
    }

    public void setDamageOpponent(Damage damageOpponent) {
        this.damageOpponent = damageOpponent;
    }

    public Damage getDamageReceive() {
        return damageReceive;
    }

    public void setDamageReceive(Damage damageReceive) {
        this.damageReceive = damageReceive;
    }

    /**
     * Indicates if the card can receive damage.
     * 
     * @return <b>true</b> if the card can be damaged; <b>false</b> otherwise.
     */
    public boolean isDamageable() {
        if (damageReceive.getDelta() == 0 && damageReceive.getFactor() == 0)
            return false;
        return true;
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
            one.applySpecialAbilityBefore(two);
        }

        if (two.useSpecialAbilityBefore) {
            two.applySpecialAbilityBefore(one);
        }

        two.getCard().applySecondCardAdvantageBeforeEvaluation(two, one);

        final String oneCombatDebug = one.getCard().getCombatDebugString(one, two);
        final String twoCombatDebug = two.getCard().getCombatDebugString(two, one);
        if (oneCombatDebug != null && !oneCombatDebug.isEmpty())
            Card.LOGGER.debug("1. " + oneCombatDebug);
        if (twoCombatDebug != null && !twoCombatDebug.isEmpty())
            Card.LOGGER.debug("2. " + twoCombatDebug);

        // Apply Attack
        Action attacker = null;
        Action receiver = null;
        if (one.getAttack() > two.getAttack()) {
            attacker = one;
            receiver = two;
        } else if (one.getAttack() < two.getAttack()) {
            attacker = two;
            receiver = one;
        }

        if (attacker != null && receiver != null) {
            // Calculate how much change in health should occur
            // Factors in the attacker's damage ability
            // Factors in the receiver's damage ability
            int deltaHealth = (attacker.getDamageOpponent().getDelta() + receiver.getDamageReceive().getDelta())
                    * attacker.getDamageOpponent().getFactor() * receiver.getDamageReceive().getFactor();

            receiver.getCard().setHealth(receiver.getCard().getHealth() + deltaHealth);
            receiver.getCard().applyPassiveAfterLostHealth(attacker, deltaHealth);
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
            one.applySpecialAbilityAfter(two);
        }

        if (two.useSpecialAbilityAfter) {
            two.applySpecialAbilityAfter(one);
        }
    }

}
