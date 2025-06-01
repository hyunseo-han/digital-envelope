package com.ddwu.hospital_reservation.controller;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
        	//1. 사용자 데이터 수집
            String name = request.getParameter("name"); //이름
            String birth = request.getParameter("birth"); //생일
            String ssn = request.getParameter("ssn"); //주민등록번호
            String department = request.getParameter("department"); // ~과
            String symptom = request.getParameter("symptom"); //증상
            String date = request.getParameter("date"); //진료예약날짜

            String reservationData = name + "," + birth + "," + ssn + "," + department + "," + symptom + "," + date; //진료예약 데이터 합치기
            byte[] dataBytes = reservationData.getBytes("UTF-8"); //byte로 변환

            //2. 사용자 개인키 불러오기
            String userPrivateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_private.key");
            PrivateKey userPrivateKey;
            try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(userPrivateKeyPath))) {
                userPrivateKey = (PrivateKey) keyIn.readObject();
            }
            
            //3. 전자서명
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(userPrivateKey);
            sig.update(dataBytes);
            byte[] signature = sig.sign();
            
            //4. 데이터와 서명 합치기
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(dataBytes);
            baos.write(signature);
            byte[] envelopeContent = baos.toByteArray();

            //5. AES 대칭키 생성하고 암호화
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedEnvelope = aesCipher.doFinal(envelopeContent);

            //6. 병원 공개키 가져오기
            String hospitalPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_public.key");
            PublicKey hospitalPublicKey;
            try (ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(hospitalPubKeyPath))) {
                hospitalPublicKey = (PublicKey) keyIn.readObject();
            }
            
            //7. AES키를 병원 공개키로 암호화
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, hospitalPublicKey);
            byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());
            
            //8. 저장
            String folderPath = getServletContext().getRealPath("/WEB-INF/reservations");
            Files.write(Paths.get(folderPath, "reservation_envelope.bin"), encryptedEnvelope);
            Files.write(Paths.get(folderPath, "reservation_key.bin"), encryptedAESKey);

            //최종 응답
            request.setAttribute("message", "예약 요청이 저장되었습니다.");
            request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}