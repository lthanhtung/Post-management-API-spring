package com.postmanagementapi.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResetTokenStore {
    private final Map<String,String> resetTokenStore = new ConcurrentHashMap<>();

    public void save(String email, String token){
        resetTokenStore.put(email,token);
    }

    public boolean verify(String email, String token){
        String storedToken = resetTokenStore.get(email);
        return storedToken != null && storedToken.equals(token);
    }

    public void remove(String email){
        resetTokenStore.remove(email);
    }
}
