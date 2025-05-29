package com.ddwu.hospital_reservation.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
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

        try {
            String folderPath = getServletContext().getRealPath("/WEB-INF/reservations");
            File folder = new File(folderPath);

            File[] envelopeFiles = folder.listFiles((dir, name) -> name.endsWith("_envelope.bin"));

            Map<String, String> reservationMap = new HashMap<>();

            for (File envelopeFile : envelopeFiles) {
                try {
                    String baseName = envelopeFile.getName().replace("_envelope.bin", "");
                    File keyFile = new File(folder, baseName + "_key.bin");

                    byte[] encryptedEnvelope = Files.readAllBytes(envelopeFile.toPath());
                    byte[] encryptedAESKey = Files.readAllBytes(keyFile.toPath());

                    // 병원 개인키 복호화
                    String privateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_private.key");
                    PrivateKey hospitalPrivateKey = KeyManager.loadPrivateKey(privateKeyPath);
                    Cipher rsaCipher = Cipher.getInstance("RSA");
                    rsaCipher.init(Cipher.DECRYPT_MODE, hospitalPrivateKey);
                    byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAESKey);
                    SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

                    // AES 복호화
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
                    byte[] decryptedContent = aesCipher.doFinal(encryptedEnvelope);

                    // 서명 분리
                    int signatureLength = 256;
                    byte[] dataBytes = Arrays.copyOfRange(decryptedContent, 0, decryptedContent.length - signatureLength);

                    // 검증은 생략 가능, 데이터만 뽑기
                    String originalData = new String(dataBytes, "UTF-8");
                    reservationMap.put(baseName, originalData);

                } catch (Exception ex) {
                    reservationMap.put(envelopeFile.getName(), "❌ 복호화 실패");
                }
            }

            request.setAttribute("reservationMap", reservationMap);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("result", "❌ 오류 발생: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }


}

