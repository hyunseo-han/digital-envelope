package com.ddwu.hospital_reservation.exception;

//Envelope 생성 실패
public class EnvelopeCreationException extends Exception {
    public EnvelopeCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
