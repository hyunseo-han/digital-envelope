package com.ddwu.hospital_reservation.util;

import java.util.Base64;

public class CryptoUtil {
    public static String encodeBase64Url(String raw) {
        return Base64.getUrlEncoder().encodeToString(raw.getBytes());
    }

    public static String decodeBase64Url(String encoded) {
        return new String(Base64.getUrlDecoder().decode(encoded));
    }
}
