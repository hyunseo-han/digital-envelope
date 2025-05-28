package com.ddwu.hospital_reservation.controller;

import java.io.IOException;
import java.util.Base64;

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
        boolean authenticated = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("hospitalAuth".equals(c.getName())) {
                    try {
                        String decoded = new String(Base64.getDecoder().decode(c.getValue()));
                        String[] parts = decoded.split(":");
                        if (parts.length == 2 && "hospital".equals(parts[0]) && "1234".equals(parts[1])) {
                            authenticated = true;
                            break;
                        }
                    } catch (Exception e) {
                        // 무시
                    }
                }
            }
        }

        if (authenticated) {
            response.sendRedirect(request.getContextPath() + "/verify-reservation");
        } else {
            request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("hospital".equals(username) && "1234".equals(password)) {
            String rawValue = username + ":" + password;
            String encodedValue = Base64.getEncoder().encodeToString(rawValue.getBytes());

            Cookie authCookie = new Cookie("hospitalAuth", encodedValue);
            authCookie.setMaxAge(60 * 60); // 1시간 유지
            authCookie.setPath("/");

            response.addCookie(authCookie);
            response.sendRedirect(request.getContextPath() + "/verify-reservation");
        } else {
            request.setAttribute("error", "로그인 실패: 올바르지 않은 정보입니다.");
            request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
        }
    }
}
