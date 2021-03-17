package io.terminus.dalaran.console.util;

import org.apache.commons.lang3.RandomStringUtils;

public class ResourceKeyUtils {

    public static String generateKey() {
        return RandomStringUtils.random(8, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN") + "-" + System.currentTimeMillis();
    }

    public static String authenticatorKey() {
        return RandomStringUtils.random(8,"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN");
    }

    public static String authenticatorValue() {
        return RandomStringUtils.random(16,"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN");
    }
}
