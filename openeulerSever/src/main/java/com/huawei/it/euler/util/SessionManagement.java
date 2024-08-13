package com.huawei.it.euler.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class SessionManagement {

    private static final int SESSION_MIN_BYTE_SIZE = 24;

    private static final int TOKEN_MIN_BYTE_SIZE = 24;

    public static byte[] generateSessionId() throws GeneralSecurityException {
        return generateSecureRandomBytes(SESSION_MIN_BYTE_SIZE);
    }

    public static String genSessionIdToHex() throws GeneralSecurityException {
        return toHex(generateSessionId());
    }

    public static byte[] generateSecureRandomBytes(int byteSize) throws GeneralSecurityException {
        SecureRandom random = genSecureRandom();
        byte[] bytes = new byte[byteSize];
        random.nextBytes(bytes);
        return bytes;
    }
    public static String generateSecureRandomBytesToHex(int byteArrayLength) throws GeneralSecurityException {
       return toHex(generateSecureRandomBytes(byteArrayLength));
    }

    public static byte[] generateToken() throws GeneralSecurityException {
        return generateSecureRandomBytes(TOKEN_MIN_BYTE_SIZE);
    }

    public static String generateTokenToHex() throws GeneralSecurityException {
        return toHex(generateToken());
    }

    public static SecureRandom genSecureRandom() throws GeneralSecurityException {
        return ThreadLocalSecureRandom.getInstance();
    }

    public static String toHex(byte[] byteArr) {
        StringBuilder builder = new StringBuilder();
        if (byteArr == null) {
            return null;
        }
        for (byte b : byteArr) {
            String hv = Integer.toHexString(b & 0xFF);
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

}