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
            File envelopeFile = new File(folderPath, "reservation_envelope.bin");
            File keyFile = new File(folderPath, "reservation_key.bin");

            if (!envelopeFile.exists() || !keyFile.exists()) {
                request.setAttribute("result", "❌ 예약 파일이 없습니다.");
            } else {
                try {
                    byte[] encryptedEnvelope = Files.readAllBytes(envelopeFile.toPath());
                    byte[] encryptedAESKey = Files.readAllBytes(keyFile.toPath());

                    // 1. 병원 개인키로 AES 키 복호화
                    String privateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_private.key");
                    PrivateKey hospitalPrivateKey = KeyManager.loadPrivateKey(privateKeyPath);

                    Cipher rsaCipher = Cipher.getInstance("RSA");
                    rsaCipher.init(Cipher.DECRYPT_MODE, hospitalPrivateKey);
                    byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAESKey);
                    SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

                    // 2. AES로 봉투 복호화
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
                    byte[] decryptedContent = aesCipher.doFinal(encryptedEnvelope);

                    // 3. 데이터와 서명 분리
                    int signatureLength = 256; // RSA 2048bit 기준
                    byte[] dataBytes = Arrays.copyOfRange(decryptedContent, 0, decryptedContent.length - signatureLength);
                    byte[] signatureBytes = Arrays.copyOfRange(decryptedContent, decryptedContent.length - signatureLength, decryptedContent.length);
                    String originalData = new String(dataBytes, "UTF-8");

                    // 4. 사용자 공개키로 서명 검증
                    String userPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_public.key");
                    PublicKey userPublicKey = KeyManager.loadPublicKey(userPubKeyPath);
                    boolean isValid = SignatureManager.verifySignature(dataBytes, signatureBytes, userPublicKey);

                    if (!isValid) {
                        request.setAttribute("result", "❌ 서명 검증 실패: 위조된 데이터일 수 있습니다.");
                    } else {
                        request.setAttribute("result", originalData);
                    }

                } catch (Exception ex) {
                    request.setAttribute("result", "❌ 복호화 실패: " + ex.getMessage());
                }
            }

        } catch (Exception e) {
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