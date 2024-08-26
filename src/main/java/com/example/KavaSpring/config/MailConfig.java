package com.example.KavaSpring.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {
    @Value("${spring.sendgrid.api-key}")
    private String apiKey;
    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(apiKey);
    }
}
