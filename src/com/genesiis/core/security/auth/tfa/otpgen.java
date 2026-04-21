package com.genesiis.core.security.auth.tfa;

import java.security.SecureRandom;

public final class OtpGen {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_DIGITS = 6;

    private OtpGen() {
    }

    public static String genarate() {
        int value = RANDOM.nextInt(1_000_000);
        return String.format("%0" + OTP_DIGITS + "d", value);
    }
}
