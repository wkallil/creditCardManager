package wkallil.card.creditCardManager.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.card.creditCardManager.dtos.CreateExpenseDTO;
import wkallil.card.creditCardManager.dtos.ExpenseDTO;
import wkallil.card.creditCardManager.mappers.ExpenseMapper;
import wkallil.card.creditCardManager.models.Expense;
import wkallil.card.creditCardManager.models.Owner;
import wkallil.card.creditCardManager.repositories.ExpenseRepository;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final OwnerService ownerService;

    private final ExpenseMapper expenseMapper;

    public ExpenseService(ExpenseRepository expenseRepository, OwnerService ownerService, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.ownerService = ownerService;
        this.expenseMapper = expenseMapper;
    }

    @Transactional
    public ExpenseDTO createExpense(CreateExpenseDTO dto) {
        Owner owner = ownerService.getOwnerEntityById(dto.getOwnerId());

        Expense expense = expenseMapper.toEntity(dto);
        expense.setOwner(owner);

        if (expense.getRecurring() == null) {
            expense.setRecurring(false);
        }

        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toDto(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByOwner(Long ownerId) {
        List<Expense> expenses = expenseRepository.findByOwnerId(ownerId);
        return expenseMapper.toDtoList(expenses);
    }
}
