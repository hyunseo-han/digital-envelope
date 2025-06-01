package com.ddwu.hospital_reservation.controller;

import com.ddwu.hospital_reservation.service.LoginService;
import com.ddwu.hospital_reservation.util.CryptoUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/hospital-login")
public class HospitalLoginServlet extends HttpServlet {

    private final LoginService loginService = new LoginService();

    //수동 로그인
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

    	//사용자 정보 가져오기
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean rememberMe = request.getParameter("rememberme") != null;

        System.out.println("[DEBUG] 로그인 요청 수신 - 입력 ID: " + username + ", 입력 PW: " + password);

        //Map으로 변환-> loginService로 위임
        Map<String, String> userMap = new HashMap<>();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("user.json")) {
            userMap = loginService.loadUserMap(in);
        } catch (Exception e) {
            request.setAttribute("error", "유저 정보를 불러오는 데 실패했습니다.");
            request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
            return;
        }

        //비밀번호 검증
        String storedHashedPw = userMap.get(username);

        //성공 시 세션 생성
        if (loginService.checkLogin(password, storedHashedPw)) {
            HttpSession session = request.getSession();
            session.invalidate(); // 기존 세션 제거
            session = request.getSession(true);
            session.setAttribute("user", username);
            session.setMaxInactiveInterval(60 * 15); // 15분

            //로그인 상태 유지 체크 시 쿠키 발급
            if (rememberMe) {
                System.out.println("[DEBUG] Remember Me 기능 실행");

                String token = java.util.UUID.randomUUID().toString();
                Map<String, String> rememberMeMap = (Map<String, String>) getServletContext().getAttribute("rememberMeMap");
                if (rememberMeMap == null) {
                    rememberMeMap = new HashMap<>();
                    getServletContext().setAttribute("rememberMeMap", rememberMeMap);
                }
                rememberMeMap.put(username, token);

                //쿠키값 cryptoUtil (username과 랜덤토큰 :로 연결)
                String raw = username + ":" + token;
                String encoded = CryptoUtil.encodeBase64Url(raw);
                Cookie loginCookie = new Cookie("rememberme", encoded);
                loginCookie.setMaxAge(60 * 60); // 1시간
                loginCookie.setPath("/");
                response.addCookie(loginCookie);
            }
            
            //성공 페이지 리다이렉트
            response.sendRedirect(request.getContextPath() + "/verify-reservation");

        } else {
            request.setAttribute("error", "로그인 실패: 올바르지 않은 정보입니다.");
            request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
        }
    }

    //쿠키 기반 자동 로그인
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        boolean authenticated = false;
        String username = null;

        Map<String, String> rememberMeMap = (Map<String, String>) getServletContext().getAttribute("rememberMeMap");
        Cookie[] cookies = request.getCookies();

        if (cookies != null && rememberMeMap != null) {
            for (Cookie c : cookies) {
                if ("rememberme".equals(c.getName())) {
                    try {
                    	//쿠키 검증 CryptoUtil
                        String decoded = CryptoUtil.decodeBase64Url(c.getValue());
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
        //인증성공 -> 세션 설정하고 리다이렉트
        if (authenticated) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", username);
            session.setMaxInactiveInterval(60 * 15);
            response.sendRedirect(request.getContextPath() + "/verify-reservation");
        } else {
        	//실패 -> 로그인 페이지 
            request.getRequestDispatcher("/WEB-INF/views/hospital_login.jsp").forward(request, response);
        }
    }
}
