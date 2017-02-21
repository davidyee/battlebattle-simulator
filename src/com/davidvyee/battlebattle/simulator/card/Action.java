package com.davidvyee.battlebattle.simulator.card;

import java.util.Optional;

/**
 * Represents an action taken by a given {@link Card}, including state
 * information such as attack strength and if a token was used or not.
 *
 * @author David Yee
 */
public class Action {
    private final Card card;
    private Card morphCard; // to support morphing
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

        public Damage(Damage copy) {
            this(copy.factor, copy.delta);
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
        useSpecialAbilityBefore = copy.useSpecialAbilityBefore;
        useSpecialAbilityAfter = copy.useSpecialAbilityAfter;
        damageOpponent = new Damage(copy.getDamageOpponent());
        damageReceive = new Damage(copy.getDamageReceive());
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

    /**
     * Morph the card into a different card. The actual morph may not be applied
     * immediately but later on during the simulation process.
     * 
     * @param morphCard
     *            The new card to morph into.
     */
    public void setMorphCard(Card morphCard) {
        this.morphCard = morphCard;
    }

    /**
     * Get the new card to morph into.
     * 
     * @return The new card wrapped within an optional if the morph was
     *         requested; otherwise, an empty optional.
     */
    public Optional<Card> getMorphCard() {
        return Optional.ofNullable(morphCard);
    }

    public boolean isUseToken() {
        return useToken;
    }

    public boolean isTokenAvailable() {
        return card.getTokens() > 0;
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

    public Action copy() {
        return new Action(this);
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

        if (one.getCard().getHealth() <= 0 || two.getCard().getHealth() <= 0) {
            // it's already game over because a card has already died
            return;
        }

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

        // Apply Token Loss
        if (one.useToken) {
            one.card.setTokens(one.card.getTokens() - 1);
        }

        if (two.useToken) {
            two.card.setTokens(two.card.getTokens() - 1);
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

        // After Hooks
        if (one.useSpecialAbilityAfter) {
            one.applySpecialAbilityAfter(two);
        }

        if (two.useSpecialAbilityAfter) {
            two.applySpecialAbilityAfter(one);
        }
    }

    /**
     * Determines whether it is best to use the given default action or the
     * bonus action, factoring in the tie-breaking card attribute and the order
     * of the actions. You need to check if the bonus action is permissible
     * (e.g. there is enough tokens to actually perform this action) before
     * applying this function to a round simulation.
     * 
     * @param opponent
     *            The opponent's default action.
     * @param myDefaultAction
     *            The player's default action.
     * @param myBonusAction
     *            The player's special action (e.g. with extra damage applied,
     *            token used, etc.)
     * @return The best of either the default action or the bonus action.
     */
    public static Action getBestActionBetweenDefaultActionAndBonusAction(Action opponent, Action myDefaultAction,
            Action myBonusAction) {
        if (myDefaultAction.getCard().equals(myBonusAction.getCard()) == false) {
            throw new IllegalArgumentException("The default action and the bonus action must be of the same card!");
        }

        myDefaultAction = myDefaultAction.copy();
        myBonusAction = myBonusAction.copy();

        myDefaultAction.setBestAction(true);
        myBonusAction.setBestAction(true);

        Action theirActionToOurDefaultAction = opponent.getCard().getBestAction(opponent, myDefaultAction);
        Action theirActionToOurBonusAction = opponent.getCard().getBestAction(opponent, myBonusAction);

        Action result;

        State theirStateToOurDefaultAction = theirActionToOurDefaultAction.getResult(myDefaultAction);
        State theirStateToOurBonusAction = theirActionToOurBonusAction.getResult(myBonusAction);
        boolean isWinnableWithoutBonus = (theirStateToOurDefaultAction == State.LOSE);
        if (myDefaultAction.getCard().isWinsTies()) {
            isWinnableWithoutBonus |= (theirStateToOurDefaultAction == State.TIE);
        }

        boolean isWinnableWithBonus = theirStateToOurBonusAction == State.LOSE
                || theirStateToOurBonusAction == State.TIE || myBonusAction.getDamageReceive().getFactor() == 0;
        boolean willLoseGoingSecond = myDefaultAction.isGoingSecond() && isWinnableWithBonus
                && theirActionToOurBonusAction.getResult(myDefaultAction) == State.WIN;
        boolean forceOpponentToUseToken = !isWinnableWithoutBonus && !isWinnableWithBonus
                && myDefaultAction.isGoingFirst() && theirActionToOurBonusAction.isUseToken();
        if (isWinnableWithoutBonus && !willLoseGoingSecond) {
            // do nothing because we'll win anyways
            result = myDefaultAction;
        } else if (isWinnableWithBonus || forceOpponentToUseToken) {
            // will win OR may just force them to also spend a token
            result = myBonusAction;
        } else {
            result = myDefaultAction;
        }

        return result;
    }
}
