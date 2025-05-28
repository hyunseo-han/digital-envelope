package com.ddwu.hospital_reservation.model;

import java.time.LocalDateTime;

public class ReservationInfo {
    private String reservationId;
    private String userId;
    private String hospitalId;
    private LocalDateTime reservationDateTime;
    private String department;
    private String doctorName;
    private String status; // 예: "예약 완료", "취소", "진료 완료"

    public ReservationInfo() {}

    public ReservationInfo(String reservationId, String userId, String hospitalId,
                           LocalDateTime reservationDateTime, String department,
                           String doctorName, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.hospitalId = hospitalId;
        this.reservationDateTime = reservationDateTime;
        this.department = department;
        this.doctorName = doctorName;
        this.status = status;
    }

    // Getter 및 Setter 메서드
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
