package com.ddwu.hospital_reservation.manager;

import java.io.*;
import java.security.*;

public class KeyManager {
    public static PrivateKey loadPrivateKey(String path) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (PrivateKey) in.readObject();
        }
    }

    public static PublicKey loadPublicKey(String path) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (PublicKey) in.readObject();
        }
    }
}