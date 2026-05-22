package com.bsn.utils;

import org.web3j.crypto.LinuxSecureRandom;

import java.security.SecureRandom;

/**
 * Desc：
 *
 * @Created by 2022-01-20 17:13
 */
public class OpbSecureRandomUtils {


    private static final SecureRandom SECURE_RANDOM;

    static {
        if (isAndroidRuntime()) {
            new LinuxSecureRandom();
        }
        SECURE_RANDOM = new SecureRandom();
    }

    static SecureRandom secureRandom() {
        return SECURE_RANDOM;
    }

    private static int isAndroid = -1;

    static boolean isAndroidRuntime() {
        if (isAndroid == -1) {
            final String runtime = System.getProperty("java.runtime.name");
            isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
        }
        return isAndroid == 1;
    }

    private OpbSecureRandomUtils() {}


}
