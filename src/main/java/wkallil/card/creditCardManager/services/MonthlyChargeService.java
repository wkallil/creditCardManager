package wkallil.card.creditCardManager.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.card.creditCardManager.dtos.MonthlyChargeDTO;
import wkallil.card.creditCardManager.dtos.OwnerSummaryDTO;
import wkallil.card.creditCardManager.mappers.MonthlyChargeMapper;
import wkallil.card.creditCardManager.models.Expense;
import wkallil.card.creditCardManager.models.MonthlyCharge;
import wkallil.card.creditCardManager.models.Owner;
import wkallil.card.creditCardManager.repositories.ExpenseRepository;
import wkallil.card.creditCardManager.repositories.MonthlyChargeRepository;
import wkallil.card.creditCardManager.repositories.OwnerRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MonthlyChargeService {

    private final MonthlyChargeRepository monthlyChargeRepository;
    private final ExpenseRepository expenseRepository;
    private final OwnerRepository ownerRepository;
    private final MonthlyChargeMapper monthlyChargeMapper;
    private final int invoiceDay;

    public MonthlyChargeService(MonthlyChargeRepository monthlyChargeRepository,
                                ExpenseRepository expenseRepository,
                                OwnerRepository ownerRepository,
                                MonthlyChargeMapper monthlyChargeMapper,
                                @Value("${credit.card.invoice.day:13}") int invoiceDay) {
        this.monthlyChargeRepository = monthlyChargeRepository;
        this.expenseRepository = expenseRepository;
        this.ownerRepository = ownerRepository;
        this.monthlyChargeMapper = monthlyChargeMapper;
        this.invoiceDay = invoiceDay;
    }

    @Transactional
    public List<MonthlyCharge> generateMonthlyCharges(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        LocalDate invoiceDate = ym.atDay(invoiceDay);

        List<Expense> activeExpenses = expenseRepository.findActiveExpensesForMonth(startDate, endDate);

        return activeExpenses.stream()
                .filter(expense ->
                        !monthlyChargeRepository.existsByExpenseIdAndReferenceMonth(expense.getId(), yearMonth))
                .map(expense -> createChargeForExpense(expense, yearMonth, invoiceDate))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public OwnerSummaryDTO getOwnerSummary(Long ownerId, String yearMonth) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        List<MonthlyCharge> charges = monthlyChargeRepository.findByOwnerIdAndReferenceMonth(ownerId, yearMonth);

        List<MonthlyChargeDTO> chargeDTOs = monthlyChargeMapper.toDtoList(charges);

        BigDecimal total = charges.stream()
                .map(MonthlyCharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate invoiceDate = charges.isEmpty()
                ? YearMonth.parse(yearMonth).atDay(invoiceDay)
                : charges.getFirst().getInvoiceDate();

        OwnerSummaryDTO summaryDTO = new OwnerSummaryDTO();
        summaryDTO.setOwnerId(owner.getId());
        summaryDTO.setOwnerName(owner.getName());
        summaryDTO.setOwnerEmail(owner.getEmail());
        summaryDTO.setReferenceMonth(yearMonth);
        summaryDTO.setInvoiceDate(invoiceDate);
        summaryDTO.setCharges(chargeDTOs);
        summaryDTO.setTotalAmount(total);

        return summaryDTO;
    }

    private MonthlyCharge createChargeForExpense(Expense expense, String yearMonth, LocalDate invoiceDate) {
        YearMonth referenceMonth = YearMonth.parse(yearMonth);
        YearMonth expenseStartMonth = YearMonth.from(expense.getStartDate());

        long monthsPassed = ChronoUnit.MONTHS.between(expenseStartMonth, referenceMonth);

        BigDecimal amount;
        Integer installmentNumber = null;

        if (expense.getRecurring()) {
            amount = expense.getPrice();
        } else {
            int currentInstallment = (int) monthsPassed + 1;
            if (currentInstallment > expense.getInstallments()) {
                return null;
            }
            installmentNumber = currentInstallment;
            amount = expense.getInstallmentAmount();
        }

        MonthlyCharge charge = new MonthlyCharge(
                expense,
                expense.getOwner(),
                amount,
                yearMonth,
                invoiceDate,
                installmentNumber
        );

        return monthlyChargeRepository.save(charge);
    }
}
