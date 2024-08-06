package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.services.SendGridEmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@AllArgsConstructor
public class SendGridEmailServiceImpl implements SendGridEmailService {

    private final SendGrid sendGridClient;

    @Override
    public Response sendEmail(String from, String to, String subject, Content content) {
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = sendGridClient.api(request);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return response;
    }

    @Override
    public void sendHtml(String from, String to, String subject, String body) {
        Response response = sendEmail(from, to, subject, new Content("text/html", body));
        logResponse(response);
    }

    private static void logResponse(Response response) {
        if (response != null) {
            log.info("Status Code: " + response.getStatusCode() + ", Body: " + response.getBody() + ", Headers: "
                    + response.getHeaders());
        }
    }
}
