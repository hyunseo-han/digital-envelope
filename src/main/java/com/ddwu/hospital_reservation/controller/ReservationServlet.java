package com.ddwu.hospital_reservation.controller;

import com.ddwu.hospital_reservation.dto.ReservationInfo;
import com.ddwu.hospital_reservation.service.ReservationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reserve")
public class ReservationServlet extends HttpServlet {

    private final ReservationService reservationService = new ReservationService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            //1. 진료예약 form 데이터 가져오기
            String name = request.getParameter("name");
            String birth = request.getParameter("birth");
            String ssn = request.getParameter("ssn");
            String department = request.getParameter("department");
            String symptom = request.getParameter("symptom");
            String date = request.getParameter("date");

            //2. 진료예약 데이터로 dto 생성 
            ReservationInfo info = new ReservationInfo(name, birth, ssn, department, symptom, date);

            //3. 키 저장 경로 설정
            String userKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/user_private.key");
            String hospitalPubKeyPath = getServletContext().getRealPath("/WEB-INF/classes/keys/hospital_public.key");
            String saveDir = getServletContext().getRealPath("/WEB-INF/reservations");

            //4. 전자봉투
            reservationService.processReservation(info, userKeyPath, hospitalPubKeyPath, saveDir);

            request.setAttribute("message", "예약 요청이 저장되었습니다.");
            request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "오류 발생: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
