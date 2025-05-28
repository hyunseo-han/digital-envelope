package com.ddwu.hospital_reservation.security;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyManager {

    // 개인키 로딩 (classpath 기준)
    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        String key = readFromResource("keys/" + filename);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    // 공개키 로딩 (classpath 기준)
    public static PublicKey loadPublicKey(String filename) throws Exception {
        String key = readFromResource("keys/" + filename);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    // classpath에서 파일 읽기
    private static String readFromResource(String resourcePath) throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다: " + resourcePath);
        }
        return new String(is.readAllBytes());
    }
}
