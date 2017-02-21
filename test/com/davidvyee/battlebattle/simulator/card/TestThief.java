package com.davidvyee.battlebattle.simulator.card;

import org.junit.Test;

import com.davidvyee.battlebattle.simulator.card.CardTester.Result;
import com.davidvyee.battlebattle.simulator.card.impl.Thief;
import com.davidvyee.battlebattle.simulator.card.impl.Weenie;

public class TestThief extends TestCard {

    @Test
    public void testThiefThief() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(10000, new Thief(), new Thief());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }
    
    @Test
    public void testThiefWeenie() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new Thief(), new Weenie());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
