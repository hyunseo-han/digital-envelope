package com.ddwu.hospital_reservation.exception;

//서명 생성 실패
public class DigitalSignatureException extends Exception {
    public DigitalSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
