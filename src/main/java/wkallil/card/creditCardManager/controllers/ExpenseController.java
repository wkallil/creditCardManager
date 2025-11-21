package wkallil.card.creditCardManager.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.card.creditCardManager.dtos.CreateExpenseDTO;
import wkallil.card.creditCardManager.dtos.ExpenseDTO;
import wkallil.card.creditCardManager.services.ExpenseService;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody CreateExpenseDTO dto) {
        ExpenseDTO expenseDTO = expenseService.createExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseDTO);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(expenseService.getExpensesByOwner(ownerId));
    }
}
