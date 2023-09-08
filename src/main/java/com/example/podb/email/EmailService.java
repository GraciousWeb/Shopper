package com.example.podb.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);


    private final JavaMailSender javaMailSender;
    public static final String TEST_CONTENT = "welcome to Arsenal";
    public static final String TEST_SUBJECT = "Confirmation message";
    public static final String SUBJECT_FORGOT_PASSWORD = "Forgot password";
    public static final String FORGOT_PASSOWRD = "welcome to Arsenal";



    @Override
    public void sendEmail (String toEmail, String subject, String content){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(toEmail);

        mailMessage.setSubject(TEST_SUBJECT);

        mailMessage.setText(TEST_CONTENT);

        javaMailSender.send(mailMessage);
    }
}



