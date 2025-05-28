package com.ddwu.hospital_reservation;

import com.ddwu.hospital_reservation.keygen.KeyGeneratorUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws Exception {
        // 경로 설정
        String resourcePath = "src/main/resources/keys";
        Path dir = Paths.get(resourcePath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // 키 파일 경로
        String userPublicKeyPath = resourcePath + "/user_public.key";
        String userPrivateKeyPath = resourcePath + "/user_private.key";
        String hospitalPublicKeyPath = resourcePath + "/hospital_public.key";
        String hospitalPrivateKeyPath = resourcePath + "/hospital_private.key";

        // 키 생성
        KeyGeneratorUtil.generateKeyPair(userPublicKeyPath, userPrivateKeyPath);
        KeyGeneratorUtil.generateKeyPair(hospitalPublicKeyPath, hospitalPrivateKeyPath);

        System.out.println("모든 키 파일이 src/main/resources/keys 폴더에 생성되었습니다.");
    }
}
