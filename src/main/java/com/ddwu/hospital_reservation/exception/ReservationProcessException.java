package com.ddwu.hospital_reservation.exception;

//예약 처리 중 예외 발생
public class ReservationProcessException extends Exception {
    public ReservationProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
