package com.postmanagementapi.service.Email;


public interface EmailService {
    void notifyUserRegistered(String to,String subject, String content );
}
