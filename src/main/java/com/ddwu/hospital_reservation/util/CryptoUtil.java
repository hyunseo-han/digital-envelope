package com.ddwu.hospital_reservation.util;

import java.util.Base64;

public class CryptoUtil {
    public static String encodeBase64Url(byte[] tokenBytes) {
        return Base64.getUrlEncoder().encodeToString(tokenBytes);
    }

    public static String decodeBase64Url(String encoded) {
        return new String(Base64.getUrlDecoder().decode(encoded));
    }
}
