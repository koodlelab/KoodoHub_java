package com.koodohub.util;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

    private static final int DEF_COUNT = 20;
    private static final Random rand = new Random();

    private RandomUtil() {
    }

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }


    public static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }
}
