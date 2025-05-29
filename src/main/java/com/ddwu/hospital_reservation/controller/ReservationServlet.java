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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String birth = request.getParameter("birth");
            String ssn = request.getParameter("ssn");
            String department = request.getParameter("department");
            String symptom = request.getParameter("symptom");
            String date = request.getParameter("date");

            String reservationData = name + "," + birth + "," + ssn + "," + department + "," + symptom + "," + date;
            byte[] dataBytes = reservationData.getBytes("UTF-8");

            // 전자서명
            String userPrivateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_private.key");
            PrivateKey userPrivateKey = KeyManager.loadPrivateKey(userPrivateKeyPath);
            byte[] digitalSignature = SignatureManager.signData(dataBytes, userPrivateKey);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(dataBytes);
            baos.write(digitalSignature);
            byte[] envelopeContent = baos.toByteArray();

            // AES 암호화
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedEnvelope = aesCipher.doFinal(envelopeContent);

            // AES 키 암호화
            String hospitalPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_public.key");
            PublicKey hospitalPublicKey = KeyManager.loadPublicKey(hospitalPubKeyPath);
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, hospitalPublicKey);
            byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());
            String folderPath = getServletContext().getRealPath("/WEB-INF/reservations");

            // 단일 파일 저장
            Files.write(Paths.get(folderPath, "reservation_envelope.bin"), encryptedEnvelope);
            Files.write(Paths.get(folderPath, "reservation_key.bin"), encryptedAESKey);

            request.setAttribute("message", "✅ 예약 요청이 저장되었습니다.");
            request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}