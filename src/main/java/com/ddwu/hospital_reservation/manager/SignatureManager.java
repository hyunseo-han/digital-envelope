package com.ddwu.hospital_reservation.manager;

import java.security.*;

import com.ddwu.hospital_reservation.exception.DigitalSignatureException;

public class SignatureManager {
    public static byte[] sign(byte[] data, PrivateKey privateKey) throws DigitalSignatureException  {
        try{
        	Signature sig = Signature.getInstance("SHA256withRSA");
	        sig.initSign(privateKey);
	        sig.update(data);
	        return sig.sign();
        } catch(Exception e) {
        	throw new DigitalSignatureException("서명 생성 실패", e);
        }
    }

    public static boolean verify(byte[] data, byte[] signature, PublicKey publicKey) throws DigitalSignatureException  {
        try{
        	Signature sig = Signature.getInstance("SHA256withRSA");
	        sig.initVerify(publicKey);
	        sig.update(data);
	        return sig.verify(signature);
        }  catch(Exception e) {
        	throw new DigitalSignatureException("서명 생성 실패", e);
        }
    }
}