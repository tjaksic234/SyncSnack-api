package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.security.utils.EmailTemplates;
import com.example.KavaSpring.services.SendGridEmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequestMapping("api/mails")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class MailController {

    private final SendGridEmailService sendGridEmailService;

    //? controller for testing the sendgrid mail service
    @PostMapping("/verify")
    public ResponseEntity<String> sendMail(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam URL companyLogoUrl) {
        try {
            log.info("Sending mail with SendGrid");
            sendGridEmailService.sendHtml(from, to, subject, EmailTemplates.confirmationEmail(to, "lmao", companyLogoUrl));
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
