package com.example.scm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.scm.services.MailService;
@Service
public class MailServiceImpl implements MailService{

    private final JavaMailSender eMailSender;
    private final String domainName;

    public MailServiceImpl(JavaMailSender eMailSender, @Value("${spring.mail.properties.mail.smtp.from}") String domainName) {
        this.eMailSender = eMailSender;
        this.domainName = domainName;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(domainName);
        eMailSender.send(message);
    }

    @Override
    public void sendEmailWithHtml() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEmailWithHtml'");
    }

    @Override
    public void sendEmailWithAttachment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEmailWithAttachment'");
    }
    
}
