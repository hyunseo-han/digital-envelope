package com.ddwu.hospital_reservation.manager;

import java.io.*;
import java.security.*;

import com.ddwu.hospital_reservation.exception.KeyLoadException;

public class KeyManager {
    public static PrivateKey loadPrivateKey(String path) throws KeyLoadException  {
        try (FileInputStream fis = new FileInputStream(path);
                ObjectInputStream in = new ObjectInputStream(fis)) {
            return (PrivateKey) in.readObject();
        }catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new KeyLoadException("개인키 로딩 실패: " + path, e);
        }
    }

    public static PublicKey loadPublicKey(String path) throws KeyLoadException  {
    	try (FileInputStream fis = new FileInputStream(path);
    	         ObjectInputStream in = new ObjectInputStream(fis)) {
            return (PublicKey) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new KeyLoadException("공개키 로딩 실패: " + path, e);
        }
    }
}