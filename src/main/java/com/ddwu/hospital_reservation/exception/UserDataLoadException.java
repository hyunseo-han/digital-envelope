package com.ddwu.hospital_reservation.exception;

//user.json 불러오기 실패
public class UserDataLoadException extends Exception {
    public UserDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
