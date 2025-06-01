package com.ddwu.hospital_reservation.controller;

import com.ddwu.hospital_reservation.manager.EnvelopeManager;
import com.ddwu.hospital_reservation.manager.KeyManager;
import com.ddwu.hospital_reservation.manager.SignatureManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.util.Arrays;

@WebServlet("/verify-reservation")
public class VerifyReservationServlet extends HttpServlet {

	private boolean isAuthenticated(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			System.out.println("[DEBUG] 세션 없음");
			return false;
		}
		Object user = session.getAttribute("user");
		System.out.println("[DEBUG] 세션 사용자 확인: " + user);
		return user != null && !user.toString().isBlank();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[DEBUG] /verify-reservation 접근");

		if (!isAuthenticated(request)) {
			System.out.println("[DEBUG] 인증 실패 - 리다이렉트");
			response.sendRedirect(request.getContextPath() + "/hospital-login");
			return;
		}

		try {
			// 1. key, 전자봉투 경로 
			String folderPath = getServletContext().getRealPath("/WEB-INF/reservations");
			File envelopeFile = new File(folderPath, "reservation_envelope.bin");
			File keyFile = new File(folderPath, "reservation_key.bin");

			if (!envelopeFile.exists() || !keyFile.exists()) {
				request.setAttribute("result", "❌ 예약 파일이 없습니다.");
			} else {
				try {
					//1. 암호화된 데이터와 AES 키 불러오기
					byte[] encryptedEnvelope = Files.readAllBytes(envelopeFile.toPath());
					byte[] encryptedAESKey = Files.readAllBytes(keyFile.toPath());
					
					//2. 병원 개인키로 AES키 복호화
					String privateKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_private.key");
	                PrivateKey hospitalPrivateKey = KeyManager.loadPrivateKey(privateKeyPath);
	                SecretKey aesKey = EnvelopeManager.decryptRSA(encryptedAESKey, hospitalPrivateKey);

	                //3. AES로 예약 데이터 복호화
	                byte[] decryptedContent = EnvelopeManager.decryptAES(encryptedEnvelope, aesKey);
	              
	                //4. 데이터, 서명 분리
	                byte[][] parts = EnvelopeManager.splitEnvelope(decryptedContent, 128);
	                byte[] dataBytes = parts[0];
	                byte[] signatureBytes = parts[1];
	                String originalData = new String(dataBytes, "UTF-8");// 출력할 수 있게

					// 5. 사용자 공개키로 서명 복호화 -> 서명 검증
	                String userPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_public.key");
	                PublicKey userPublicKey = KeyManager.loadPublicKey(userPubKeyPath);
	                boolean isValid = SignatureManager.verify(dataBytes, signatureBytes, userPublicKey);

					if (!isValid) {
						request.setAttribute("result", "서명 검증 실패: 위조된 데이터일 수 있습니다.");
					} else {
						request.setAttribute("result", originalData);
					}

				} catch (Exception ex) {
					request.setAttribute("result", "복호화 실패: " + ex.getMessage());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("result", "오류 발생: " + e.getMessage());
		}

		request.getRequestDispatcher("/WEB-INF/views/verify_reservation.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
