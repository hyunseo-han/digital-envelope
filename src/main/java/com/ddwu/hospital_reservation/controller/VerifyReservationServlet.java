package com.ddwu.hospital_reservation.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.ddwu.hospital_reservation.security.KeyManager;
import com.ddwu.hospital_reservation.security.SignatureManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
			String folderPath = getServletContext().getRealPath("/WEB-INF/reservations");
			File envelopeFile = new File(folderPath, "reservation_envelope.bin");
			File keyFile = new File(folderPath, "reservation_key.bin");

			if (!envelopeFile.exists() || !keyFile.exists()) {
				request.setAttribute("result", "❌ 예약 파일이 없습니다.");
			} else {
				try {
					byte[] encryptedEnvelope = Files.readAllBytes(envelopeFile.toPath());
					byte[] encryptedAESKey = Files.readAllBytes(keyFile.toPath());

					String privateKeyPath = getServletContext()
							.getRealPath("/WEB-INF/classes/keys/hospital_private.key");
					PrivateKey hospitalPrivateKey = KeyManager.loadPrivateKey(privateKeyPath);

					Cipher rsaCipher = Cipher.getInstance("RSA");
					rsaCipher.init(Cipher.DECRYPT_MODE, hospitalPrivateKey);
					byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAESKey);
					SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

					Cipher aesCipher = Cipher.getInstance("AES");
					aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
					byte[] decryptedContent = aesCipher.doFinal(encryptedEnvelope);

					int signatureLength = 256;
					byte[] dataBytes = Arrays.copyOfRange(decryptedContent, 0,
							decryptedContent.length - signatureLength);
					byte[] signatureBytes = Arrays.copyOfRange(decryptedContent,
							decryptedContent.length - signatureLength, decryptedContent.length);
					String originalData = new String(dataBytes, "UTF-8");

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
