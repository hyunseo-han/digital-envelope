package com.ddwu.hospital_reservation.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/hospital-login")
public class HospitalLoginServlet extends HttpServlet {

	private final Map<String, String> rememberMeMap = new HashMap<>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		boolean authenticated = false;
		String username = null;

		Cookie[] cookies = request.getCookies();
		Map<String, String> rememberMeMap = (Map<String, String>) getServletContext().getAttribute("rememberMeMap");

		if (cookies != null && rememberMeMap != null) {
			for (Cookie c : cookies) {
				if ("rememberme".equals(c.getName())) {
					try {
						String decoded = new String(Base64.getUrlDecoder().decode(c.getValue()),
								StandardCharsets.UTF_8);
						String[] parts = decoded.split(":");
						if (parts.length == 2) {
							String id = parts[0];
							String token = parts[1];
							if (token.equals(rememberMeMap.get(id))) {
								authenticated = true;
								username = id;
								break;
							}
						}
					} catch (Exception e) {
						// 무시
					}
				}
			}
		}

		if (authenticated) {
			HttpSession session = request.getSession(true);
			session.setAttribute("user", username);
			session.setMaxInactiveInterval(60 * 15); // 15분

			response.sendRedirect(request.getContextPath() + "/verify-reservation");
		} else {
			request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		boolean rememberMe = request.getParameter("rememberme") != null;

		if ("hospital".equals(username) && "1234".equals(password)) {

			// 세션 고정 방지: 기존 세션 무효화 후 새로 생성
			HttpSession session = request.getSession();
			session.invalidate(); // 세션을 강제로 무효화하고 새로 생성하여 Session Fixation 공격 방지
			session = request.getSession(true);
			session.setAttribute("user", username); // 인증 후 HttpSession에 사용자 정보를 저장
			session.setMaxInactiveInterval(60 * 15); // 15분

			if (rememberMe) {
				String randomToken = UUID.randomUUID().toString();

				// 전역 저장소에 저장
				Map<String, String> rememberMeMap = (Map<String, String>) getServletContext()
						.getAttribute("rememberMeMap");
				if (rememberMeMap == null) {
					rememberMeMap = new HashMap<>();
					getServletContext().setAttribute("rememberMeMap", rememberMeMap);
				}
				rememberMeMap.put(username, randomToken);

				String raw = username + ":" + randomToken;
				String encoded = Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
				Cookie loginCookie = new Cookie("rememberme", encoded);
				loginCookie.setMaxAge(60 * 60); // 1시간
				loginCookie.setPath("/");
				response.addCookie(loginCookie);
			}

			response.sendRedirect(request.getContextPath() + "/verify-reservation");
		} else {
			request.setAttribute("error", "로그인 실패: 올바르지 않은 정보입니다.");
			request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
		}
	}
}
