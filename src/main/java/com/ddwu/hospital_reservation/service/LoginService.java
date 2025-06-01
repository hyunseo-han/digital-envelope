package com.ddwu.hospital_reservation.service;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginService {
    public Map<String, String> loadUserMap(InputStream in) throws Exception {
        if (in == null) throw new Exception("user.json not found");
        String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        JSONObject obj = new JSONObject(json);
        Map<String, String> map = new HashMap<>();
        for (String key : obj.keySet()) {
            map.put(key, obj.getString(key));
        }
        return map;
    }

    public boolean checkLogin(String inputPassword, String storedHashedPw) {
        return storedHashedPw != null && BCrypt.checkpw(inputPassword, storedHashedPw);
    }
}
