package io.terminus.dalaran.console.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class GenerateKeyUtils {

    public static String resourceKey() {
        return RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN") + "-" + System.currentTimeMillis();
    }

    public static String resourceKey(String tenantCode) {
        return tenantCode + "@" + RandomStringUtils.random(4, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN") + "-" + System.currentTimeMillis();
    }

    public static String resourceKey(String origin, String tenantCode) {
        String key = StringUtils.substringAfter(origin, "@");
        return tenantCode + "@" + key;
    }

    public static String authenticatorKey() {
        return RandomStringUtils.random(8,"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN");
    }

    public static String authenticatorValue() {
        return RandomStringUtils.random(16,"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN1234567890");
    }
}
