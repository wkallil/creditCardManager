package wkallil.card.creditCardManager.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.card.creditCardManager.dtos.OwnerSummaryDTO;
import wkallil.card.creditCardManager.models.MonthlyCharge;
import wkallil.card.creditCardManager.services.MonthlyChargeService;

import java.util.List;

@RestController
@RequestMapping("/api/monthly-charges")
public class MonthlyChargeController {

    private final MonthlyChargeService monthlyChargeService;

    public MonthlyChargeController(MonthlyChargeService monthlyChargeService) {
        this.monthlyChargeService = monthlyChargeService;
    }

    @PostMapping("/generate/{yearMonth}")
    public ResponseEntity<List<MonthlyCharge>> generateCharges(@PathVariable String yearMonth) {
        List<MonthlyCharge> charges = monthlyChargeService.generateMonthlyCharges(yearMonth);
        return ResponseEntity.ok(charges);
    }

    @GetMapping("/{yearMonth}/owner/{ownerId}")
    public ResponseEntity<OwnerSummaryDTO> getOwnerSummary(
            @PathVariable String yearMonth,
            @PathVariable Long ownerId
    ) {
        OwnerSummaryDTO summaryDTO = monthlyChargeService.getOwnerSummary(ownerId, yearMonth);
        return ResponseEntity.ok(summaryDTO);
    }
}
