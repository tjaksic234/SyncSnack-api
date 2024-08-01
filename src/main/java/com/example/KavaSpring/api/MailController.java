package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.security.mail.MailgunEmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/mails")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class MailController {

    private final MailgunEmailService emailService;

    @PostMapping("/send")
    public String sendMail(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content) {
        try {
            emailService.sendEmailWithHtmlContent(from, to, subject, content);
            return "Email sent successfully!";
        } catch (Exception e) {
            throw new RuntimeException("Error sending mail", e);
        }
    }
}
