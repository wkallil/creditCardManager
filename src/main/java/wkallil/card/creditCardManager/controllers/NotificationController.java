package wkallil.card.creditCardManager.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wkallil.card.creditCardManager.dtos.MonthlyChargeDTO;
import wkallil.card.creditCardManager.dtos.OwnerDTO;
import wkallil.card.creditCardManager.services.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notifications", description = "Send notification emails to owners")
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(summary = "Send Monthly Notifications to All Owners (yyyy-MM)",
            description = "Sends a standardized email notification to every registered owner in the Monthly Charge",
            tags = {"Notifications"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(
                            schema = @Schema(implementation = MonthlyChargeDTO.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Already Exists", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping("/send/{yearMonth}")
    public ResponseEntity<String> sendMonthlyNotifications(@PathVariable String yearMonth) {
        emailService.sendMonthlyNotifications(yearMonth);
        return ResponseEntity.ok("Monthly notifications sent for " + yearMonth);
    }

    @Operation(summary = "Send Monthly Notifications to a specific Owner by ID (yyyy-MM) ",
            description = "Sends an email notification to the owner identified by the provided ID",
            tags = {"Notifications"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(
                            schema = @Schema(implementation = MonthlyChargeDTO.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Already Exists", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "No Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping("/send/{yearMonth}/owner/{ownerId}")
    public ResponseEntity<String> sendMonthlyNotificationForOwner(@PathVariable String yearMonth, @PathVariable Long ownerId) {
        emailService.sendMonthlyNotificationForOwner(ownerId, yearMonth);
        return ResponseEntity.ok("Monthly notification sent for owner " + ownerId + " for " + yearMonth);
    }
}
