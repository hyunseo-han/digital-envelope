package com.ddwu.hospital_reservation.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
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
        	
            // 1. 클라이언트로부터 두 개의 Base64 인코딩 데이터 수신
        	
            String envelopeBase64 = request.getParameter("envelope");
            String encryptedAESKeyBase64 = request.getParameter("encryptedKey");
            System.out.println("🏥 병원이 수신한 AES Key: " + encryptedAESKeyBase64);
            
            byte[] encryptedEnvelope = Base64.getDecoder().decode(envelopeBase64);
            byte[] encryptedAESKey = Base64.getDecoder().decode(encryptedAESKeyBase64);

            // 2. 병원 개인키 로딩 (병원만이 이 키로 AES 키 복호화 가능)
            String privateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_private.key");
            PrivateKey hospitalPrivateKey = KeyManager.loadPrivateKey(privateKeyPath);

            // 3. AES 키 복호화 (RSA로 복호화)
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.DECRYPT_MODE, hospitalPrivateKey);
            byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAESKey);

            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

            // 4. 전자봉투 복호화
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedContent = aesCipher.doFinal(encryptedEnvelope);

            // 5. 예약 정보와 서명 분리
            int signatureLength = 256;
            byte[] dataBytes = Arrays.copyOfRange(decryptedContent, 0, decryptedContent.length - signatureLength);
            byte[] signature = Arrays.copyOfRange(decryptedContent, decryptedContent.length - signatureLength, decryptedContent.length);

            // 6. user 공개키로 서명 검증
            String pubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_public.key");
            PublicKey publicKey = KeyManager.loadPublicKey(pubKeyPath);

            boolean isValid = SignatureManager.verifySignature(dataBytes, signature, publicKey);

            request.setAttribute("result", isValid ? "✅ 전자서명 검증 성공: 예약자 본인 맞음" : "❌ 전자서명 검증 실패: 위조 가능성 있음");
            request.setAttribute("originalData", new String(dataBytes, "UTF-8"));

            request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("result", "❌ 오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);
        }
    }

}
