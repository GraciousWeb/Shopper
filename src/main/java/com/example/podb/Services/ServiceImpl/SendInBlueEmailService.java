//package com.example.podb.Services.ServiceImpl;
//
//import lombok.extern.slf4j.Slf4j;
//import sendinblue.ApiClient;
//import sendinblue.ApiException;
//import sendinblue.Configuration;
//import sendinblue.auth.ApiKeyAuth;
//import sibModel.SendSmtpEmail;
//import sibApi.TransactionalEmailsApi;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import sibModel.SendSmtpEmailReplyTo;
//import sibModel.SendSmtpEmailSender;
//import sibModel.SendSmtpEmailTo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Slf4j
//public class SendInBlueEmailService {
//
//    @Value("${sendinblue.api.key}")
//    private String apiKey;
//    @Value("${sendinblue.sender.email}")
//    private String senderEmail;
//
//    @Value("${sendinblue.sender.name}")
//    private String senderName;
//
//    @Value("${sendinblue.replyto.email}")
//    private String replyToEmail;
//    public void sendVerificationEmail(String recipientEmail, String verificationLink) throws ApiException {
//        if (recipientEmail == null || recipientEmail.isEmpty()) {
//            log.error("Failed to send email due to invalid recipient email.");
//            throw new IllegalArgumentException("Recipient email cannot be null or empty.");
//        }
//        if (verificationLink == null || verificationLink.isEmpty()) {
//            log.error("Failed to send email due to invalid verification link.");
//            throw new IllegalArgumentException("Verification link cannot be null or empty.");
//        }
//
//        log.info("Preparing to send verification email to {}", recipientEmail);
//
//        ApiClient defaultClient = Configuration.getDefaultApiClient();
//        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
//        apiKeyAuth.setApiKey(apiKey);
//
//        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();
//
//        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
//
//        SendSmtpEmailSender sender = new SendSmtpEmailSender();
//        sender.setEmail(senderEmail);
//        sender.setName(senderName);
//        sendSmtpEmail.setSender(sender);
//
//        List<SendSmtpEmailTo> toList = new ArrayList<>();
//        SendSmtpEmailTo to = new SendSmtpEmailTo();
//        to.setEmail(recipientEmail);
//        toList.add(to);
//        sendSmtpEmail.setTo(toList);
//
//        SendSmtpEmailReplyTo replyTo = new SendSmtpEmailReplyTo();
//        replyTo.setEmail(replyToEmail);
//        replyTo.setName("Your Reply-To Name");
//        sendSmtpEmail.setReplyTo(replyTo);
//
//        sendSmtpEmail.setSubject("Email Verification");
//        sendSmtpEmail.setHtmlContent("<p>Please click on the link below to verify your email:</p><a href='" + verificationLink + "'>Verify</a>");
//
//        apiInstance.sendTransacEmail(sendSmtpEmail);
//    }
//}
//
