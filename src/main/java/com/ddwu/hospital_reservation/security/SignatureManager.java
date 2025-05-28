package com.ddwu.hospital_reservation.security;

import java.security.*;

public class SignatureManager {

    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    // 전자서명 생성
    public static byte[] signData(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    // 전자서명 검증
    public static boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}