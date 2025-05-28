package com.ddwu.hospital_reservation.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.ddwu.hospital_reservation.security.KeyManager;
import com.ddwu.hospital_reservation.security.SignatureManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/verify-reservation")
public class VerifyReservationServlet extends HttpServlet {

    private boolean isAuthenticated(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;

        for (Cookie c : cookies) {
            if ("hospitalAuth".equals(c.getName())) {
                try {
                    String decoded = new String(Base64.getDecoder().decode(c.getValue()));
                    String[] parts = decoded.split(":");
                    if (parts.length == 2 && "hospital".equals(parts[0]) && "1234".equals(parts[1])) {
                        return true;
                    }
                } catch (Exception e) {
                    // 무효한 쿠키 값인 경우
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/hospital-login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/hospital-login");
            return;
        }

        try {
            String envelopeBase64 = request.getParameter("envelope");
            byte[] encryptedEnvelope = Base64.getDecoder().decode(envelopeBase64);

            // AES 키 불러오기 (예제에서는 임시 키 생성. 실제론 고정 키 또는 키 전송 방식 사용해야 함)
//            SecretKey aesKey = KeyGenerator.getInstance("/WEB-INF/classes/keys/hospital_pivate.key").generateKey();

            String aesKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_private.key");
            byte[] keyBytes = Files.readAllBytes(Paths.get(aesKeyPath)); // AES 키는 16, 24, 또는 32 바이트여야 함
            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");
            
            
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedContent = aesCipher.doFinal(encryptedEnvelope);

            int signatureLength = 256; // RSA 2048bit 기준
            byte[] dataBytes = Arrays.copyOfRange(decryptedContent, 0, decryptedContent.length - signatureLength);
            byte[] signature = Arrays.copyOfRange(decryptedContent, decryptedContent.length - signatureLength, decryptedContent.length);

            String pubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_public.key");
            PublicKey publicKey = KeyManager.loadPublicKey(pubKeyPath);

            boolean isValid = SignatureManager.verifySignature(dataBytes, signature, publicKey);

            request.setAttribute("result", isValid ? "전자서명 검증 성공: 예약자 본인 맞음" : "전자서명 검증 실패: 위조 가능성 있음");
            request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("result", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);
        }
    }
}
