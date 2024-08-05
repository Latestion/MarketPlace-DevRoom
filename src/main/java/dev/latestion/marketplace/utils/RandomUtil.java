package dev.latestion.marketplace.utils;

import java.util.Random;

public final class RandomUtil {

    public static final Random RANDOM = new Random();

    public static int getRandInt(int min, int max) throws IllegalArgumentException {
        if (min == max) {
            return min;
        }
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static double getRandDouble(double min, double max) throws IllegalArgumentException {
        if (min == max) {
            return min;
        }

        return RANDOM.nextDouble() * (max - min) + min;
    }

    public static boolean getChance(double chance) {
        return chance >= 100.0 || chance >= RandomUtil.getRandDouble(0.0, 100.0);
    }

}
