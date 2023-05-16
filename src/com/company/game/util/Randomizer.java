package com.company.game.util;

import java.util.Random;

public class Randomizer {
    private static Random random = new Random();

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static int nextInt() {
        return random.nextInt();
    }

    public static Random getRandom() {
        return random;
    }
}
