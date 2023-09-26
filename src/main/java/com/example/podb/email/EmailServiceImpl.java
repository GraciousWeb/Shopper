package com.example.podb.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);


    private final JavaMailSender javaMailSender;
    public static final String TEST_CONTENT = "Click on the link to verify your email";
    public static final String TEST_SUBJECT = "Verify Email";




    @Override
    public void sendEmail (String toEmail, String subject, String content){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(toEmail);

        mailMessage.setSubject(subject);

        mailMessage.setText(content);
        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}



