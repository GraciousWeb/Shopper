package com.example.podb.email;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String content);
}

