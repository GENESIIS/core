package com.genesiis.core.security.auth.tfa;

import java.security.SecureRandom;

public final class otpgen {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_DIGITS = 6;

    private otpgen() {
    }

    public static String send() {
        int value = RANDOM.nextInt(1_000_000);
        return String.format("%0" + OTP_DIGITS + "d", value);
    }
}
