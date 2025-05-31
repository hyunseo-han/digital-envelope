package com.ddwu.hospital_reservation.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/hospital-login")
public class HospitalLoginServlet extends HttpServlet {

	private Map<String, String> loadUserMap() throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream("user.json");
		if (in == null) {
			throw new IOException("user.json not found in classpath");
		}
		String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		JSONObject obj = new JSONObject(json);
		Map<String, String> map = new HashMap<>();
		for (String key : obj.keySet()) {
			map.put(key, obj.getString(key));
		}
		return map;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		boolean rememberMe = request.getParameter("rememberme") != null;

		System.out.println("[DEBUG] 로그인 요청 수신 - 입력 ID: " + username + ", 입력 PW: " + password);

		Map<String, String> userMap = loadUserMap();
		String storedHashedPw = userMap.get(username);

		if (storedHashedPw != null) {
			System.out.println("[DEBUG] 저장된 해시: " + storedHashedPw);
			System.out.println("[DEBUG] 비밀번호 일치 여부: " + BCrypt.checkpw(password, storedHashedPw));
		} else {
			System.out.println("[DEBUG] 아이디 '" + username + "'에 대한 해시 없음");
		}

		if (storedHashedPw != null && BCrypt.checkpw(password, storedHashedPw)) {
			HttpSession session = request.getSession();
			session.invalidate();
			session = request.getSession(true);
			session.setAttribute("user", username);
			session.setMaxInactiveInterval(60 * 15);

			System.out.println("[DEBUG] 로그인 성공, 세션 등록됨 - 사용자: " + username);

			if (rememberMe) {
				System.out.println("[DEBUG] '로그인 상태 유지' 체크됨 - Remember Me 기능 수행");

				String token = java.util.UUID.randomUUID().toString();
				Map<String, String> rememberMeMap = (Map<String, String>) getServletContext()
						.getAttribute("rememberMeMap");
				if (rememberMeMap == null) {
					rememberMeMap = new HashMap<>();
					getServletContext().setAttribute("rememberMeMap", rememberMeMap);
				}
				rememberMeMap.put(username, token);

				String raw = username + ":" + token;
				String encoded = java.util.Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
				Cookie loginCookie = new Cookie("rememberme", encoded);
				loginCookie.setMaxAge(60 * 60); // 1시간
				loginCookie.setPath("/");
				response.addCookie(loginCookie);

				System.out.println("[DEBUG] RememberMe 쿠키 발급 완료: " + encoded);
			}

			response.sendRedirect(request.getContextPath() + "/verify-reservation");

		} else {
			System.out.println("[DEBUG] 로그인 실패 - ID/PW 불일치 또는 없음");
			request.setAttribute("error", "로그인 실패: 올바르지 않은 정보입니다.");
			request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean authenticated = false;
		String username = null;

		Map<String, String> rememberMeMap = (Map<String, String>) getServletContext().getAttribute("rememberMeMap");
		Cookie[] cookies = request.getCookies();

		if (cookies != null && rememberMeMap != null) {
			for (Cookie c : cookies) {
				if ("rememberme".equals(c.getName())) {
					try {
						String decoded = new String(java.util.Base64.getUrlDecoder().decode(c.getValue()),
								StandardCharsets.UTF_8);
						String[] parts = decoded.split(":");
						if (parts.length == 2 && parts[1].equals(rememberMeMap.get(parts[0]))) {
							authenticated = true;
							username = parts[0];
							break;
						}
					} catch (Exception e) {
						System.out.println("[DEBUG] RememberMe 쿠키 파싱 실패: " + e.getMessage());
					}
				}
			}
		}

		if (authenticated) {
			HttpSession session = request.getSession(true);
			session.setAttribute("user", username);
			session.setMaxInactiveInterval(60 * 15);
			System.out.println("[DEBUG] RememberMe 인증 성공, 사용자: " + username);
			response.sendRedirect(request.getContextPath() + "/verify-reservation");
		} else {
			System.out.println("[DEBUG] 인증되지 않음 - 로그인 화면으로 이동");
			request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
		}
	}
}
