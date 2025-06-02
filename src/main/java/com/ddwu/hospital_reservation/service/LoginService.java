package com.ddwu.hospital_reservation.service;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.ddwu.hospital_reservation.exception.ReservationProcessException;
import com.ddwu.hospital_reservation.exception.UserDataLoadException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginService {
	//user.json에서 Map으로 변환
    public Map<String, String> loadUserMap(InputStream in) throws UserDataLoadException {
        try{
        	if (in == null) throw new Exception("user.json not found");
	        String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
	        JSONObject obj = new JSONObject(json);
	        Map<String, String> map = new HashMap<>();
	        for (String key : obj.keySet()) {
	            map.put(key, obj.getString(key));
	        }
	        return map;
        } catch(Exception e) {
        	throw new UserDataLoadException("user.json 불러오기 실패", e);
        }
        
    }
    
    //비밀번호 검증 로직
    public boolean checkLogin(byte[] inputPassword, String storedHashedPw) {
        try {
            String inputAsString = new String(inputPassword, StandardCharsets.UTF_8); //byte -> String 변환
            return storedHashedPw != null && BCrypt.checkpw(inputAsString, storedHashedPw);
        } finally {
            clearArray(inputPassword);
        }
    }
    
    // 메모리에서 즉시 삭제
    private void clearArray(byte[] a) {
        if (a != null) {
            for (int i = 0; i < a.length; i++) a[i] = 0;
        }
    }


}
