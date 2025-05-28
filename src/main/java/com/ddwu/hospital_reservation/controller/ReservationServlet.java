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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 1. 사용자 입력 수집
            String name = request.getParameter("name");
            String birth = request.getParameter("birth");
            String department = request.getParameter("department");
            String symptom = request.getParameter("symptom");
            String date = request.getParameter("date");

            String reservationData = name + "," + birth + "," + department + "," + symptom + "," + date;
            byte[] dataBytes = reservationData.getBytes("UTF-8");

            System.out.println("예약 요청 수신: " + reservationData);

            // ✅ 2. 전자서명 생성 (절대 경로 기반으로 수정)
            String keyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_private.key");
            PrivateKey userPrivateKey = KeyManager.loadPrivateKey(keyPath);

            byte[] digitalSignature = SignatureManager.signData(dataBytes, userPrivateKey);
            System.out.println("전자서명 생성 완료, 서명 길이: " + digitalSignature.length + " bytes");

            // 3. 예약정보 + 서명을 하나로 결합
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(dataBytes);
            baos.write(digitalSignature);
            byte[] envelopeContent = baos.toByteArray();

            // 4. AES 대칭키 생성
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();

         // 5. AES로 봉투 암호화
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedEnvelope = aesCipher.doFinal(envelopeContent);

            // ✅ 6. AES 키를 병원 공개키로 암호화
            String hospitalPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_public.key");
            PublicKey hospitalPublicKey = KeyManager.loadPublicKey(hospitalPubKeyPath);

            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, hospitalPublicKey);
            byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());

            System.out.println("AES 키 암호화 완료, 길이: " + encryptedAESKey.length + " bytes");

            // 병원으로 전송할 두 개의 바이너리 출력 (Base64로도 가능)
            request.setAttribute("result", "예약 처리 완료<br><br>" +
                    "전자봉투(Base64):<br>" + Base64.getEncoder().encodeToString(encryptedEnvelope) + "<br><br>" +
                    "암호화된 AES 키(Base64):<br>" + Base64.getEncoder().encodeToString(encryptedAESKey));

            request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);


        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);

        }
    }
}
