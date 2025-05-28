package com.ddwu.hospital_reservation.controller;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. 예약 정보 입력 수집
            String name = request.getParameter("name");
            String birth = request.getParameter("birth");
            String department = request.getParameter("department");
            String symptom = request.getParameter("symptom");
            String date = request.getParameter("date");

            String reservationData = name + "," + birth + "," + department + "," + symptom + "," + date;
            byte[] dataBytes = reservationData.getBytes("UTF-8");

            // 2. 전자서명
            String userPrivateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_private.key");
            PrivateKey userPrivateKey = KeyManager.loadPrivateKey(userPrivateKeyPath);
            byte[] digitalSignature = SignatureManager.signData(dataBytes, userPrivateKey);

            // 3. 데이터 + 서명 결합
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(dataBytes);
            baos.write(digitalSignature);
            byte[] envelopeContent = baos.toByteArray();

            // 4. AES 암호화
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedEnvelope = aesCipher.doFinal(envelopeContent);

            // 5. AES 키를 병원 공개키로 암호화
            String hospitalPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_public.key");
            PublicKey hospitalPublicKey = KeyManager.loadPublicKey(hospitalPubKeyPath);

            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, hospitalPublicKey);
            byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());

            // 6. 병원 검증 URL로 자동 POST 요청
            String envelopeBase64 = Base64.getEncoder().encodeToString(encryptedEnvelope);
            String encryptedAESKeyBase64 = Base64.getEncoder().encodeToString(encryptedAESKey);

            URL url = new URL("http://localhost:8080/hospital-reservation/verify-reservation");
            
         // 1. 쿠키용 인증 정보 Base64 인코딩
            String rawAuth = "hospital:1234";
            String encodedAuth = Base64.getEncoder().encodeToString(rawAuth.getBytes());

            // 2. POST 요청 구성 + 쿠키 전송
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // ✅ 3. 병원 인증 쿠키 붙이기
            conn.setRequestProperty("Cookie", "hospitalAuth=" + encodedAuth);

            // 4. 데이터 구성
            String postData = "envelope=" + java.net.URLEncoder.encode(envelopeBase64, "UTF-8")
                    + "&encryptedKey=" + java.net.URLEncoder.encode(encryptedAESKeyBase64, "UTF-8");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes());
            }


            // 응답 스트림 → JSP로 전달
            String resultMessage = new String(conn.getInputStream().readAllBytes(), "UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(resultMessage);

            System.out.println("🔐 예약자가 생성한 Encrypted AES Key: " + encryptedAESKeyBase64);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}