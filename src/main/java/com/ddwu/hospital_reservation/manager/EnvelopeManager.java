
package com.ddwu.hospital_reservation.manager;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.ddwu.hospital_reservation.exception.DecryptionException;
import com.ddwu.hospital_reservation.exception.EncryptionException;
import com.ddwu.hospital_reservation.exception.EnvelopeCreationException;
import com.ddwu.hospital_reservation.exception.KeyGenerationException;

import java.io.ByteArrayOutputStream;
import java.security.*;
import java.util.Arrays;

public class EnvelopeManager {
    public static byte[] createEnvelope(byte[] data, byte[] signature) throws EnvelopeCreationException  {
    	try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(data);
            baos.write(signature);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new EnvelopeCreationException("Envelope 생성 실패", e);
        }
    }

    public static byte[][] splitEnvelope(byte[] envelope, int signatureLength) {
        byte[] data = Arrays.copyOfRange(envelope, 0, envelope.length - signatureLength);
        byte[] signature = Arrays.copyOfRange(envelope, envelope.length - signatureLength, envelope.length);
        return new byte[][] { data, signature };
    }

    public static SecretKey generateAESKey() throws KeyGenerationException {
    	try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new KeyGenerationException("AES 키 생성 실패", e);
        }
    }

    public static byte[] encryptAES(byte[] data, SecretKey key)  throws EncryptionException {
    	try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new EncryptionException("AES 암호화 실패", e);
        }
    }

    public static byte[] decryptAES(byte[] encrypted, SecretKey key) throws DecryptionException {
    	 try {
             Cipher cipher = Cipher.getInstance("AES");
             cipher.init(Cipher.DECRYPT_MODE, key);
             return cipher.doFinal(encrypted);
         } catch (Exception e) {
             throw new DecryptionException("AES 복호화 실패", e);
         }
    }

    public static byte[] encryptRSA(byte[] keyBytes, PublicKey publicKey) throws EncryptionException  {
    	try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(keyBytes);
        } catch (Exception e) {
            throw new EncryptionException("RSA 암호화 실패", e);
        }
    }

    public static SecretKey decryptRSA(byte[] encryptedKey, PrivateKey privateKey) throws DecryptionException  {
    	try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] keyBytes = cipher.doFinal(encryptedKey);
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new DecryptionException("RSA 복호화 실패", e);
        }
    }
}
