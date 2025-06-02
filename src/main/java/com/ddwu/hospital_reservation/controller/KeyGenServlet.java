package com.ddwu.hospital_reservation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import com.ddwu.hospital_reservation.exception.KeyGenerationException;

@WebServlet("/generate-keys")
public class KeyGenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            //1. 키 저장할 경로
            String resourcePath = getServletContext().getRealPath("/WEB-INF/classes/keys");
            Path dir = Paths.get(resourcePath);

            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                System.out.println("[DEBUG] 키 저장 디렉토리 생성됨: " + resourcePath);
            }

            //2. 사용자 키쌍 생성
            KeyPair userPair = generateRSAKeyPair();
            saveKeyToFile(resourcePath + "/user_public.key", userPair.getPublic());
            saveKeyToFile(resourcePath + "/user_private.key", userPair.getPrivate());

            //3. 병원측 키쌍 생성
            KeyPair hospitalPair = generateRSAKeyPair();
            saveKeyToFile(resourcePath + "/hospital_public.key", hospitalPair.getPublic());
            saveKeyToFile(resourcePath + "/hospital_private.key", hospitalPair.getPrivate());

            request.setAttribute("message", "키 생성이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "키 생성 중 오류 발생: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }

    //키쌍 생성 메서드
    private KeyPair generateRSAKeyPair() throws KeyGenerationException {
        try{
        	KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	        keyGen.initialize(1024);
	        return keyGen.generateKeyPair();
        } catch(Exception e) {
        	throw new KeyGenerationException("AES 키 생성 실패", e);
        }
    }

    //.key 파일로 저장하는 메서드
    private void saveKeyToFile(String path, Object key) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(key);
            System.out.println("[DEBUG] 키 저장됨: " + path);
        }
    }
}
