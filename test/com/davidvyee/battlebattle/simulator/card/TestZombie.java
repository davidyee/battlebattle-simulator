package com.davidvyee.battlebattle.simulator.card;

import org.junit.Test;

import com.davidvyee.battlebattle.simulator.card.CardTester.Result;
import com.davidvyee.battlebattle.simulator.card.impl.Barbarian;
import com.davidvyee.battlebattle.simulator.card.impl.Robot;
import com.davidvyee.battlebattle.simulator.card.impl.Weenie;
import com.davidvyee.battlebattle.simulator.card.impl.Zombie;

public class TestZombie extends TestCard {

    @Test
    public void testZombieRobot() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new Zombie(), new Robot());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }
    
    @Test
    public void testZombieBarbarian() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new Zombie(), new Barbarian());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }
    
    @Test
    public void testZombieWeenie() throws InstantiationException, IllegalAccessException {
        Result[] results = CardTester.runTest(1, new Zombie(), new Weenie());
        LOGGER.info(CardTester.getFormattedOutput(results));
    }

}
