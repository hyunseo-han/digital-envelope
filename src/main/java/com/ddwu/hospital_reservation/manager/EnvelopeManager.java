package com.ddwu.hospital_reservation.manager;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.util.Arrays;

public class EnvelopeManager {
    public static byte[] createEnvelope(byte[] data, byte[] signature) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(data);
        baos.write(signature);
        return baos.toByteArray();
    }

    public static byte[][] splitEnvelope(byte[] envelope, int signatureLength) {
        byte[] data = Arrays.copyOfRange(envelope, 0, envelope.length - signatureLength);
        byte[] signature = Arrays.copyOfRange(envelope, envelope.length - signatureLength, envelope.length);
        return new byte[][] { data, signature };
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static byte[] encryptAES(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decryptAES(byte[] encrypted, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }

    public static byte[] encryptRSA(byte[] keyBytes, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(keyBytes);
    }

    public static SecretKey decryptRSA(byte[] encryptedKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] keyBytes = cipher.doFinal(encryptedKey);
        return new SecretKeySpec(keyBytes, "AES");
    }
}