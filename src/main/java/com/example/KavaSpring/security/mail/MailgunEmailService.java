package com.example.KavaSpring.security.mail;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailgunEmailService {

    @Value("${MAILGUN_API_KEY}")
    private String apiKey;

    @Value("${MAILGUN_BASEURL}")
    private String baseUrl;

    @Value("${MAILGUN_DOMAIN}")
    private String domain;

    public void sendEmailWithHtmlContent(String from, String to, String subject, String content) {
        MailgunMessagesApi mailgunMessagesApi = MailgunClient.config(baseUrl, apiKey).createApi(MailgunMessagesApi.class);

        Message message = Message.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(content)
                .build();

        MessageResponse response = mailgunMessagesApi.sendMessage(domain, message);

        log.info("ID: " + response.getId() + ", Message: " + response.getMessage());
    }
}
