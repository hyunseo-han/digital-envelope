package com.ddwu.hospital_reservation.security;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyManager {

    // 개인키 로딩 (파일 시스템 경로 기준)
    public static PrivateKey loadPrivateKey(String absolutePath) throws Exception {
        String key = readFromFileSystem(absolutePath);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    // 공개키 로딩 (파일 시스템 경로 기준)
    public static PublicKey loadPublicKey(String absolutePath) throws Exception {
        String key = readFromFileSystem(absolutePath);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    // 파일 시스템에서 파일 읽기
    private static String readFromFileSystem(String path) throws IOException {
        return Files.readString(Paths.get(path));
    }
}
