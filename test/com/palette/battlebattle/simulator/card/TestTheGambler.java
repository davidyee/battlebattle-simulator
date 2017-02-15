package com.palette.battlebattle.simulator.card;

import org.junit.Test;

import com.palette.battlebattle.simulator.card.CardTester.Result;
import com.palette.battlebattle.simulator.card.impl.Robot;
import com.palette.battlebattle.simulator.card.impl.TheGambler;

public class TestTheGambler extends TestCard {

    @Test
    public void testTheGamblerRobot() throws InstantiationException, IllegalAccessException {
        Result[] results = ct.runTest(1, TheGambler.class, Robot.class);
        LOGGER.info(ct.getFormattedOutput(results));
    }

}
