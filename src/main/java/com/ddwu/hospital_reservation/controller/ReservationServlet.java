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
import java.nio.file.Files;
import java.nio.file.Paths;
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
            String ssn = request.getParameter("ssn");
            
            String reservationData = name + "," + birth + "," + ssn + "," + department + "," + symptom + "," + date;
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

            // 6. 병원 서버로 자동 전송하지 않고 서버에 파일로 저장
            String folderPath = getServletContext().getRealPath("/WEB-INF/reservations");
            Files.createDirectories(Paths.get(folderPath)); // 폴더 없으면 생성

            // 각각 Base64 없이 그대로 저장
            Files.write(Paths.get(folderPath, "encrypted-envelope.bin"), encryptedEnvelope);
            Files.write(Paths.get(folderPath, "encrypted-key.bin"), encryptedAESKey);

            // 결과 페이지로 안내
            request.setAttribute("message", "✅ 예약 요청이 암호화되어 병원에 저장되었습니다. 병원 로그인 후 복호화를 진행하세요.");
            request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}