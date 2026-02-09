package com.postmanagementapi.service.Email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailServiceImp implements EmailService{

    private final JavaMailSender mailSender;

    @Override
    public void notifyUserRegistered(String to, String Subject, String content) {
        String subject = "THÔNG BÁO %s THÀNH CÔNG".formatted(Subject);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        mailSender.send(mailMessage);

    }
}
