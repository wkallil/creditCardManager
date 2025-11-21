package wkallil.card.creditCardManager.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wkallil.card.creditCardManager.services.EmailService;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send/{yearMonth}")
    public ResponseEntity<String> sendMonthlyNotifications(@PathVariable String yearMonth) {
        emailService.sendMonthlyNotifications(yearMonth);
        return ResponseEntity.ok("Monthly notifications sent for " + yearMonth);
    }
}
