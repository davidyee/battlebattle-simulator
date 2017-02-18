package com.palette.battlebattle.simulator.card;

import org.junit.Test;

import com.palette.battlebattle.simulator.card.CardTester.Result;
import com.palette.battlebattle.simulator.card.impl.Clone;
import com.palette.battlebattle.simulator.card.impl.Robot;
import com.palette.battlebattle.simulator.card.impl.TheRuler;

public class TestClone extends TestCard {

    @Test
    public void testCloneRobot() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, Clone.class, Robot.class);
        LOGGER.info(CardTester.getFormattedOutput(results));
    }
    
    @Test
    public void testCloneTheRuler() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, Clone.class, TheRuler.class);
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
