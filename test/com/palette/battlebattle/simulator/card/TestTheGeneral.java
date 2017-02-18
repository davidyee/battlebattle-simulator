package com.palette.battlebattle.simulator.card;

import org.junit.Test;

import com.palette.battlebattle.simulator.card.CardTester.Result;
import com.palette.battlebattle.simulator.card.impl.TheGeneral;
import com.palette.battlebattle.simulator.card.impl.Vanilla;

public class TestTheGeneral extends TestCard {

    @Test
    public void testTheGeneralVanilla() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, TheGeneral.class, Vanilla.class);
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
