package com.ddwu.hospital_reservation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class HospitalLogoutServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1. 세션 무효화
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		// 2. rememberme 쿠키 삭제
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if ("rememberme".equals(c.getName())) {
					c.setValue(""); // 값 비우기
					c.setPath("/"); // 경로 명확히 지정
					c.setMaxAge(0); // 즉시 만료
					response.addCookie(c); // 변경 적용
				}
			}
		}

		// 3. 로그인 페이지로 이동
		response.sendRedirect(request.getContextPath() + "/hospital-login");
	}

}
