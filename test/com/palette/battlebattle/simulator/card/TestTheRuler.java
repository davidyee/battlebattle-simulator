package com.palette.battlebattle.simulator.card;

import org.junit.Test;

import com.palette.battlebattle.simulator.card.CardTester.Result;
import com.palette.battlebattle.simulator.card.impl.Assassin;
import com.palette.battlebattle.simulator.card.impl.TheRuler;

public class TestTheRuler extends TestCard {

    @Test
    public void testTheRulerTheRuler() throws InstantiationException, IllegalAccessException {
        Result[] results = ct.runTest(1, TheRuler.class, TheRuler.class);
        LOGGER.info(ct.getFormattedOutput(results));
    }

    @Test
    public void testTheRulerAssassin() throws InstantiationException, IllegalAccessException {
        Result[] results = ct.runTest(1, TheRuler.class, Assassin.class);
        LOGGER.info(ct.getFormattedOutput(results));
    }

}
