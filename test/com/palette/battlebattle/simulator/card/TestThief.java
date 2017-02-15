package com.palette.battlebattle.simulator.card;

import org.junit.Test;

import com.palette.battlebattle.simulator.card.CardTester.Result;
import com.palette.battlebattle.simulator.card.impl.Thief;

public class TestThief extends TestCard {

    @Test
    public void testThiefThief() throws InstantiationException, IllegalAccessException {
        Result[] results = ct.runTest(10000, Thief.class, Thief.class);
        LOGGER.info(ct.getFormattedOutput(results));
    }

}
