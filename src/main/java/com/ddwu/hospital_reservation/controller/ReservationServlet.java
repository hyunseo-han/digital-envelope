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
            // 1. 예약 정보 입력 수집
            String name = request.getParameter("name");
            String birth = request.getParameter("birth");
            String ssn = request.getParameter("ssn"); // 새로 추가된 필드
            String department = request.getParameter("department");
            String symptom = request.getParameter("symptom");
            String date = request.getParameter("date");

            // 2. 문자열 결합에 주민등록번호 포함
            String reservationData = name + "," + birth + "," + ssn + "," + department + "," + symptom + "," + date;
            byte[] dataBytes = reservationData.getBytes("UTF-8");

            // 3. 전자서명 생성
            String userPrivateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_private.key");
            PrivateKey userPrivateKey = KeyManager.loadPrivateKey(userPrivateKeyPath);
            byte[] digitalSignature = SignatureManager.signData(dataBytes, userPrivateKey);

            // 4. 데이터 + 서명 결합
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(dataBytes);
            baos.write(digitalSignature);
            byte[] envelopeContent = baos.toByteArray();

            // 5. AES 암호화
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedEnvelope = aesCipher.doFinal(envelopeContent);

            // 6. AES 키를 병원 공개키로 암호화
            String hospitalPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_public.key");
            PublicKey hospitalPublicKey = KeyManager.loadPublicKey(hospitalPubKeyPath);

            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, hospitalPublicKey);
            byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());

            // 7. 서버에 파일로 저장
            String folderPath = getServletContext().getRealPath("/WEB-INF/reservations");
            Files.createDirectories(Paths.get(folderPath));
            Files.write(Paths.get(folderPath, "encrypted-envelope.bin"), encryptedEnvelope);
            Files.write(Paths.get(folderPath, "encrypted-key.bin"), encryptedAESKey);

            // 8. 결과 페이지로 이동
            request.setAttribute("message", "✅ 예약 요청이 암호화되어 병원에 저장되었습니다. 병원 로그인 후 복호화를 진행하세요.");
            request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}

