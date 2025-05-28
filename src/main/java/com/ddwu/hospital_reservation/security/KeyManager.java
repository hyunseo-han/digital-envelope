package com.ddwu.hospital_reservation.security;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyManager {

    // Base64 인코딩된 개인키 로딩
    public static PrivateKey loadPrivateKey(String filepath) throws Exception {
        String key = readFromFile(filepath);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    // Base64 인코딩된 공개키 로딩
    public static PublicKey loadPublicKey(String filepath) throws Exception {
        String key = readFromFile(filepath);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private static String readFromFile(String path) throws IOException {
        return new String(java.nio.file.Files.readAllBytes(new File(path).toPath()));
    }
}