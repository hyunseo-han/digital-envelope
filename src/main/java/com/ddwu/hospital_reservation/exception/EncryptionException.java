package com.ddwu.hospital_reservation.exception;

//암호화 실패
public class EncryptionException extends Exception {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
