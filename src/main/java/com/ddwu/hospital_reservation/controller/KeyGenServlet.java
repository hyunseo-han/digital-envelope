package com.ddwu.hospital_reservation.controller;

import com.ddwu.hospital_reservation.keygen.KeyGeneratorUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

            String userPublicKeyPath = resourcePath + "/user_public.key";
            String userPrivateKeyPath = resourcePath + "/user_private.key";
            String hospitalPublicKeyPath = resourcePath + "/hospital_public.key";
            String hospitalPrivateKeyPath = resourcePath + "/hospital_private.key";

            KeyGeneratorUtil.generateKeyPair(userPublicKeyPath, userPrivateKeyPath);
            KeyGeneratorUtil.generateKeyPair(hospitalPublicKeyPath, hospitalPrivateKeyPath);

            request.setAttribute("message", "키 생성이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "키 생성 중 오류 발생: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}
