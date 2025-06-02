package com.ddwu.hospital_reservation.exception;

//키 생성 실패
public class KeyGenerationException extends Exception {
    public KeyGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
