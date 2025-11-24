package wkallil.card.creditCardManager.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.card.creditCardManager.dtos.CreateExpenseDTO;
import wkallil.card.creditCardManager.dtos.ExpenseDTO;
import wkallil.card.creditCardManager.services.ExpenseService;

import java.util.List;

@Tag(name = "Expenses", description = "Operations related to expenses")
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "Creates a new expense",
            description = "Registers a new expense in the system based on the provided data.",
            tags = {"Expenses"},
            responses = {
                    @ApiResponse(description = "Created: Expense successfully created", responseCode = "201", content = @Content(
                            schema = @Schema(implementation = ExpenseDTO.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody CreateExpenseDTO dto) {
        ExpenseDTO expenseDTO = expenseService.createExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseDTO);
    }

    @Operation(summary = "Retrieves expenses for an owner by ID",
            description = "Returns a list of all recorded expenses associated with the specified owner ID.",
            tags = {"Expenses"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(
                            schema = @Schema(implementation = ExpenseDTO.class)
                    )),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
            }
    )
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(expenseService.getExpensesByOwner(ownerId));
    }
}
