package com.ddwu.hospital_reservation;

import com.ddwu.hospital_reservation.keygen.KeyGeneratorUtil;

public class App {
    public static void main(String[] args) throws Exception {
        KeyGeneratorUtil.generateKeyPair("user_public.key", "user_private.key");
        KeyGeneratorUtil.generateKeyPair("hospital_public.key", "hospital_private.key");
    }
}
