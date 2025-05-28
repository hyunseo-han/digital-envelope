package com.ddwu.hospital_reservation.controller;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.ddwu.hospital_reservation.security.KeyManager;
import com.ddwu.hospital_reservation.security.SignatureManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/verify-reservation")
public class VerifyReservationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Boolean auth = (Boolean) request.getSession().getAttribute("authenticated");
        if (auth != null && auth) {
            request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/hospital-login");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 로그인 인증 체크
            Boolean auth = (Boolean) request.getSession().getAttribute("authenticated");
            if (auth == null || !auth) {
                response.sendRedirect(request.getContextPath() + "/hospital-login");
                return;
            }

            String envelopeBase64 = request.getParameter("envelope");
            byte[] encryptedEnvelope = Base64.getDecoder().decode(envelopeBase64);

            // AES 키 불러오기 (고정 키 사용 예제)
            SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey(); // 실제론 공유된 키 써야 함

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedContent = aesCipher.doFinal(encryptedEnvelope);

            int signatureLength = 256; // RSA 2048bit
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
