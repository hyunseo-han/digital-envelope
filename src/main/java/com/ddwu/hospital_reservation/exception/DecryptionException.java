package com.ddwu.hospital_reservation.exception;

//복호화 실패
public class DecryptionException extends Exception {
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
