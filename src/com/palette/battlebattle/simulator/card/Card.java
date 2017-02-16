package com.palette.battlebattle.simulator.card;

import java.util.Random;

import org.apache.log4j.Logger;

/**
 * Represents a vanilla <code>Card</code> in the BattleBattle card game.
 *
 * @author David Yee
 */
public abstract class Card {
    protected static final Logger LOGGER = Logger.getLogger(Card.class);

    protected int health;
    protected int tokens;

    protected final boolean winsTies;

    private final Random random = new Random();

    public Card(int health, int tokens) {
        this(health, tokens, false);
    }

    public Card(int health, int tokens, boolean winsTies) {
        super();
        this.health = health;
        this.tokens = tokens;
        this.winsTies = winsTies;
    }

    /**
     * <p>
     * Get the best action to take given the current roll and their roll. This
     * function acts as an estimator before the card state is actually changed.
     * </p>
     * <p>
     * <b> Note that this function should typically not modify the token count
     * or health of the card as it will be factored in later by
     * {@link Action#evaluateActions(Action, Action)}!</b>
     * </p>
     * 
     * @param myRoll
     * @param theirRoll
     * @return
     */
    public abstract Action getBestAction(Action myRoll, Action theirRoll);

    /**
     * The passive ability that acts dependent on the given final actions. This
     * method is called before the two cards have attacked each other.
     * 
     * @param myAction
     *            The player's final action.
     * @param opponentAction
     *            The opponent's final action.
     */
    public void applyPassiveBeforeEvaluation(Action myAction, Action opponentAction) {
        return;
    }

    /**
     * The passive ability that acts dependent on the given final actions. This
     * method is called after the two cards have attacked each other.
     * 
     * @param myAction
     *            The player's final action.
     * @param opponentAction
     *            The opponent's final action.
     */
    public void applyPassiveAfterEvaluation(Action myAction, Action opponentAction) {
        return;
    }

    /**
     * Apply the advantage that a second card may receive since it played after
     * the first card. This method is always run; therefore, any pre-condition
     * checks need to be done within the method body itself. For example, if the
     * second card advantage is only invoked when a token is used then you must
     * check that here.
     * 
     * @param myAction
     *            The player's final action.
     * @param opponentAction
     *            The opponent's final action.
     */
    public void applySecondCardAdvantageBeforeEvaluation(Action myAction, Action opponentAction) {
        return;
    }

    /**
     * Called when the card loses health as a result of losing a battle against
     * the attacking card.
     * 
     * @param attacker
     *            The attacking card that won the round.
     * @param deltaHealth
     *            The amount of health gained or lost (delta amount).
     */
    public void applyPassiveAfterLostHealth(Action attacker, int deltaHealth) {
        return;
    }

    /**
     * Get the string that appears during combat to print out while debugging.
     * 
     * @param finalAction
     * @param finalOpponentAction
     * @return
     */
    public String getCombatDebugString(Action finalAction, Action finalOpponentAction) {
        return "";
    }

    /**
     * Update the state of the card dependent on the performed roll. This method
     * is typically called once per round at the beginning. In most cards, this
     * method will add tokens to the card depending on what was rolled.
     * 
     * @param roll
     *            The rolled value.
     */
    public void applyResultOfRoll(int roll) {
        return;
    }

    /**
     * Roll a dice.
     * 
     * @return The result of the roll. By default, this is a uniformly random
     *         number between 1 and 6.
     */
    public int roll() {
        return random.nextInt(7) + 1;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public int getTokens() {
        return tokens;
    }

    public boolean isWinsTies() {
        return winsTies;
    }
    
}
