package com.davidvyee.battlebattle.simulator.card;

import org.junit.Test;

import com.davidvyee.battlebattle.simulator.card.CardTester;
import com.davidvyee.battlebattle.simulator.card.CardTester.Result;
import com.davidvyee.battlebattle.simulator.card.impl.Assassin;
import com.davidvyee.battlebattle.simulator.card.impl.TheRuler;

public class TestTheRuler extends TestCard {

    @Test
    public void testTheRulerTheRuler() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new TheRuler(), new TheRuler());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

    @Test
    public void testTheRulerAssassin() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new TheRuler(), new Assassin());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
