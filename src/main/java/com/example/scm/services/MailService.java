package com.example.scm.services;

public interface MailService {
    void sendEmail(String to, String subject, String body);

    void sendEmailWithHtml();

    void sendEmailWithAttachment();
}
