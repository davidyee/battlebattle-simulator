package com.davidvyee.battlebattle.simulator.card;

import org.junit.Test;

import com.davidvyee.battlebattle.simulator.card.CardTester.Result;
import com.davidvyee.battlebattle.simulator.card.impl.Barbarian;
import com.davidvyee.battlebattle.simulator.card.impl.Clone;
import com.davidvyee.battlebattle.simulator.card.impl.Robot;
import com.davidvyee.battlebattle.simulator.card.impl.TheRuler;

public class TestClone extends TestCard {

    @Test
    public void testCloneRobot() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new Clone(), new Robot());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

    @Test
    public void testCloneTheRuler() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new Clone(), new TheRuler());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

    @Test
    public void testCloneBarbarian() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new Clone(), new Barbarian());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
