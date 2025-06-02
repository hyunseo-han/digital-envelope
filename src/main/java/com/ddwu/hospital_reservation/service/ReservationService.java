package com.ddwu.hospital_reservation.service;

import com.ddwu.hospital_reservation.dto.ReservationInfo;
import com.ddwu.hospital_reservation.exception.ReservationProcessException;
import com.ddwu.hospital_reservation.manager.*;

import javax.crypto.SecretKey;
import java.nio.file.*;
import java.security.*;

public class ReservationService {
    public void processReservation(ReservationInfo info, String userKeyPath, String hospitalKeyPath, String saveDir) throws ReservationProcessException {
        try{
        	byte[] dataBytes = info.toCSV().getBytes("UTF-8");

        PrivateKey userPrivateKey = KeyManager.loadPrivateKey(userKeyPath);
        PublicKey hospitalPublicKey = KeyManager.loadPublicKey(hospitalKeyPath);

        byte[] signature = SignatureManager.sign(dataBytes, userPrivateKey);
        byte[] envelopeContent = EnvelopeManager.createEnvelope(dataBytes, signature);

        SecretKey aesKey = EnvelopeManager.generateAESKey();
        byte[] encryptedEnvelope = EnvelopeManager.encryptAES(envelopeContent, aesKey);
        byte[] encryptedKey = EnvelopeManager.encryptRSA(aesKey.getEncoded(), hospitalPublicKey);

        Files.write(Paths.get(saveDir, "reservation_envelope.bin"), encryptedEnvelope);
        Files.write(Paths.get(saveDir, "reservation_key.bin"), encryptedKey);
        } catch (Exception e) {
        	throw new ReservationProcessException("예약 처리 중 오류 발생", e);
        }
   }
}