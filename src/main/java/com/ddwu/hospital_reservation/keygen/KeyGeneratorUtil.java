package com.ddwu.hospital_reservation.keygen;

import java.security.*;
import java.io.*;
import java.util.Base64;

/**
 * RSA 공개키/개인키를 생성하고 파일로 저장하는 유틸리티 클래스
 */
public class KeyGeneratorUtil {

    /**
     * RSA 키 쌍을 생성하고, 지정된 경로에 저장한다.
     *
     * @param publicKeyPath 저장할 공개키 파일 경로
     * @param privateKeyPath 저장할 개인키 파일 경로
     */
    public static void generateKeyPair(String publicKeyPath, String privateKeyPath) throws Exception {
        // RSA 2048비트 키쌍 생성
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // 키 추출
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Base64 인코딩하여 파일로 저장
        writeToFile(publicKeyPath, Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        writeToFile(privateKeyPath, Base64.getEncoder().encodeToString(privateKey.getEncoded()));

        System.out.println("RSA 키 생성 완료:");
        System.out.println("공개키 경로: " + publicKeyPath);
        System.out.println("개인키 경로: " + privateKeyPath);
    }

    private static void writeToFile(String path, String keyContent) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(keyContent);
        }
    }
}

