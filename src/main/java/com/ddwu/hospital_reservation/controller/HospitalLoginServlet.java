package com.ddwu.hospital_reservation.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/hospital-login")
public class HospitalLoginServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 쿠키가 존재하면 자동 로그인 처리
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("hospitalAuth".equals(c.getName())) {
                    String value = c.getValue(); // ex: hospital;1234
                    String[] parts = value.split(";");
                    if (parts.length == 2 && "hospital".equals(parts[0]) && "1234".equals(parts[1])) {
                        // 쿠키 유효 → 자동 로그인
                        response.sendRedirect(request.getContextPath() + "/verify-reservation");
                        return;
                    }
                }
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("hospital".equals(username) && "1234".equals(password)) {
            // 쿠키 생성 (유효기간 1시간)
            Cookie authCookie = new Cookie("hospitalAuth", username + ";" + password);
            authCookie.setMaxAge(60 * 60); // 1시간 유지
            authCookie.setPath("/"); // 전체 경로에서 유효

            response.addCookie(authCookie);
            response.sendRedirect(request.getContextPath() + "/verify-reservation");
        } else {
            request.setAttribute("error", "로그인 실패: 올바르지 않은 정보입니다.");
            request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
        }
    }
}
