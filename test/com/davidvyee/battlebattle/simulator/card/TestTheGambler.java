package com.davidvyee.battlebattle.simulator.card;

import org.junit.Test;

import com.davidvyee.battlebattle.simulator.card.CardTester;
import com.davidvyee.battlebattle.simulator.card.CardTester.Result;
import com.davidvyee.battlebattle.simulator.card.impl.Robot;
import com.davidvyee.battlebattle.simulator.card.impl.TheGambler;

public class TestTheGambler extends TestCard {

    @Test
    public void testTheGamblerRobot() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new TheGambler(), new Robot());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
