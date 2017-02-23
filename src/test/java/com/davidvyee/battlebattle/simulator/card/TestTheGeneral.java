package com.davidvyee.battlebattle.simulator.card;

import org.junit.Test;

import com.davidvyee.battlebattle.simulator.card.CardTester.Result;
import com.davidvyee.battlebattle.simulator.card.impl.Robot;
import com.davidvyee.battlebattle.simulator.card.impl.TheGeneral;
import com.davidvyee.battlebattle.simulator.card.impl.Vanilla;

public class TestTheGeneral extends TestCard {

    @Test
    public void testTheGeneralVanilla() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new TheGeneral(), new Vanilla());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }
    
    @Test
    public void testTheGeneralRobot() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new TheGeneral(), new Robot());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
