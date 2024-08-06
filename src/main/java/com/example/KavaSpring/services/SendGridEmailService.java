package com.example.KavaSpring.services;

import com.sendgrid.Response;
import com.sendgrid.helpers.mail.objects.Content;

public interface SendGridEmailService {

    Response sendEmail(String from, String to, String subject, Content content);
    void sendHtml(String from, String to, String subject, String body);
}
