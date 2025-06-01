package com.ddwu.hospital_reservation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

@WebServlet("/generate-keys")
public class KeyGenServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String resourcePath = getServletContext().getRealPath("/WEB-INF/classes/keys");
            Path dir = Paths.get(resourcePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            //사용자 키 저장
            KeyPairGenerator userKeyGen = KeyPairGenerator.getInstance("RSA");
            userKeyGen.initialize(1024);
            KeyPair userPair = userKeyGen.generateKeyPair();
            
            try (ObjectOutputStream pubOut = new ObjectOutputStream(new FileOutputStream(resourcePath + "/user_public.key"))) {
                pubOut.writeObject(userPair.getPublic());
            }
            try (ObjectOutputStream privOut = new ObjectOutputStream(new FileOutputStream(resourcePath + "/user_private.key"))) {
                privOut.writeObject(userPair.getPrivate());
            }
            
            //병원 키 저장
            KeyPairGenerator hospitalKeyGen = KeyPairGenerator.getInstance("RSA");
            hospitalKeyGen.initialize(1024);
            KeyPair hospitalPair = hospitalKeyGen.generateKeyPair();

            try (ObjectOutputStream pubOut = new ObjectOutputStream(new FileOutputStream(resourcePath + "/hospital_public.key"))) {
                pubOut.writeObject(hospitalPair.getPublic());
            }
            try (ObjectOutputStream privOut = new ObjectOutputStream(new FileOutputStream(resourcePath + "/hospital_private.key"))) {
                privOut.writeObject(hospitalPair.getPrivate());
            }

            request.setAttribute("message", "키 생성이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "키 생성 중 오류 발생: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
