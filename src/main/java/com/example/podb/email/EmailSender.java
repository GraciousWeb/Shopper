package com.example.podb.email;

public interface EmailSender {
    void sendEmail(String toEmail, String subject, String content);
}

