package com.ddwu.hospital_reservation.dto;

public class ReservationInfo {
    private String name;
    private String birth;
    private String ssn;
    private String department;
    private String symptom;
    private String date;

    public ReservationInfo(String name, String birth, String ssn, String department, String symptom, String date) {
        this.name = name;
        this.birth = birth;
        this.ssn = ssn;
        this.department = department;
        this.symptom = symptom;
        this.date = date;
    }

    public String toCSV() {
        return String.join(",", name, birth, ssn, department, symptom, date);
    }
}