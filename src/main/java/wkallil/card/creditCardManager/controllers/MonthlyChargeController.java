package wkallil.card.creditCardManager.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.card.creditCardManager.dtos.OwnerSummaryDTO;
import wkallil.card.creditCardManager.dtos.MonthlyChargeDTO;
import wkallil.card.creditCardManager.services.MonthlyChargeService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "MonthlyCharges", description = "Generate and retrieve monthly charges and summaries")
@RestController
@RequestMapping("/api/monthly-charges")
public class MonthlyChargeController {

    private final MonthlyChargeService monthlyChargeService;
    private static final Logger logger = LoggerFactory.getLogger(MonthlyChargeController.class);

    public MonthlyChargeController(MonthlyChargeService monthlyChargeService) {
        this.monthlyChargeService = monthlyChargeService;
    }

    @Operation(summary = "Generates monthly charges for a period (yyyy-MM)",
            description = "Triggers the generation of charges for all owners for the specified year and month. Returns the list of generated charges.",
            tags = {"MonthlyCharges"},
            responses = {
                    @ApiResponse(description = "Success: Charges generated and returned", responseCode = "200", content = @Content(
                            schema = @Schema(implementation = MonthlyChargeDTO.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping("/generate/{yearMonth}")
    public ResponseEntity<List<MonthlyChargeDTO>> generateCharges(@PathVariable String yearMonth) {
        logger.info("generateCharges called for {}", yearMonth);
        List<MonthlyChargeDTO> charges = monthlyChargeService.generateMonthlyCharges(yearMonth);
        logger.info("generateCharges returning {} charges", charges == null ? 0 : charges.size());
        return ResponseEntity.ok(charges);
    }

    @Operation(summary = "Returns a financial summary for an owner for a given month/year",
            description = "Retrieves the aggregated financial summary for a specific owner in the specified month (yyyy-MM).",
            tags = {"MonthlyCharges"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(
                            schema = @Schema(implementation = OwnerSummaryDTO.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @GetMapping("/{yearMonth}/owner/{ownerId}")
    public ResponseEntity<OwnerSummaryDTO> getOwnerSummary(
            @PathVariable String yearMonth,
            @PathVariable Long ownerId
    ) {
        OwnerSummaryDTO summaryDTO = monthlyChargeService.getOwnerSummary(ownerId, yearMonth);
        return ResponseEntity.ok(summaryDTO);
    }
}
