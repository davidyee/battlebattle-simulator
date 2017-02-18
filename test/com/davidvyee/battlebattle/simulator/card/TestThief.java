package com.davidvyee.battlebattle.simulator.card;

import org.junit.Test;

import com.davidvyee.battlebattle.simulator.card.CardTester;
import com.davidvyee.battlebattle.simulator.card.CardTester.Result;
import com.davidvyee.battlebattle.simulator.card.impl.Thief;

public class TestThief extends TestCard {

    @Test
    public void testThiefThief() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(10000, new Thief(), new Thief());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
