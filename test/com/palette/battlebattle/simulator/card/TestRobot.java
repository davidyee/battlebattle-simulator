package com.palette.battlebattle.simulator.card;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.palette.battlebattle.simulator.Simulator;
import com.palette.battlebattle.simulator.card.Card;
import com.palette.battlebattle.simulator.card.CardTester.Result;
import com.palette.battlebattle.simulator.card.impl.Robot;
import com.palette.battlebattle.simulator.card.impl.Thief;

public class TestRobot extends TestCard {

    @Test
    public void testRobotThief() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, Robot.class, Thief.class);
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

    /**
     * Verifies that a robot against itself is supposed to tie.
     */
    @Test
    public void testRobotRobotTied() {
        int rounds = 10;

        Optional<Card> maybeResult;

        int robot1Wins = 0;
        int robot2Wins = 0;
        int ties = 0;
        for (int i = 0; i < rounds; ++i) {
            Card robot1 = new Robot();
            Card robot2 = new Robot();

            maybeResult = Simulator.playCards(robot1, robot2);
            if (maybeResult.isPresent()) {
                Card result = maybeResult.get();
                if (result == robot1)
                    ++robot1Wins;
                else if (result == robot2)
                    ++robot2Wins;
            } else {
                ++ties;
            }
        }

        assertEquals(robot1Wins, 0);
        assertEquals(robot2Wins, 0);
        assertEquals(ties, rounds);
    }

}
