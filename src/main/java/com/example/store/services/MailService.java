package com.example.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from.email}")
    private String fromEmail;

    public void send (String toEmail, String subject, String message) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom(fromEmail);
        emailMessage.setTo(toEmail);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        try {
            mailSender.send(emailMessage);
        } catch (MailException e) {
            // runtime exception; compiler will not force you to handle it
        }
    }

    public void send(String subject, String message, String... toEmail) {
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setFrom(fromEmail);
        emailMessage.setTo(toEmail);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        try {
            mailSender.send(emailMessage);
        } catch (MailException e) {
            // runtime exception; compiler will not force you to handle it
        }
    }

    public void prepareAndSend(String subject, String message, String... toEmail) {
        MimeMessagePreparator messagePrep = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "Windows-1251");
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(message, true);
        };
        try {
            mailSender.send(messagePrep);
        } catch (MailException e) {
            // runtime exception; compiler will not force you to handle it
        }
    }
}
