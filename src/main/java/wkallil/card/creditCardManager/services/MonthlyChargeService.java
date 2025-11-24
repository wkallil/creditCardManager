package wkallil.card.creditCardManager.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.card.creditCardManager.controllers.MonthlyChargeController;
import wkallil.card.creditCardManager.dtos.MonthlyChargeDTO;
import wkallil.card.creditCardManager.dtos.OwnerSummaryDTO;
import wkallil.card.creditCardManager.exceptions.OwnerNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class MonthlyChargeService {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyChargeService.class);

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
    public List<MonthlyChargeDTO> generateMonthlyCharges(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate referenceMonthFirstDay = ym.atDay(1);
        YearMonth prevYm = ym.minusMonths(1);
        LocalDate startDate = prevYm.atDay(Math.min(invoiceDay, prevYm.lengthOfMonth()));
        LocalDate endDate = ym.atDay(Math.min(invoiceDay, ym.lengthOfMonth()));
        LocalDate endOfReferenceMonth = ym.atEndOfMonth();
        LocalDate invoiceDate = ym.atDay(Math.min(invoiceDay, ym.lengthOfMonth()));

        List<Expense> expensesToProcess = expenseRepository.findActiveExpensesForMonth(endOfReferenceMonth);

        List<MonthlyCharge> createdOrUpdated = new ArrayList<>();
        for (Expense expense : expensesToProcess) {
            logger.debug("Processing expense id={} name={} startDate={} installments={} recurring={}",
                    expense.getId(), expense.getName(), expense.getStartDate(), expense.getInstallments(), expense.getRecurring());
            MonthlyCharge mc = upsertChargeForExpense(expense, referenceMonthFirstDay, yearMonth, invoiceDate, startDate, endDate);
            if (mc != null) {
                createdOrUpdated.add(mc);
                logger.info("Included expense id={} in monthly charges (charge id={})", expense.getId(), mc.getId());
            } else {
                logger.debug("Expense id={} was skipped for reference {}", expense.getId(), referenceMonthFirstDay);
            }
        }

        return monthlyChargeMapper.toDtoList(createdOrUpdated);
    }

    @Transactional
    public OwnerSummaryDTO getOwnerSummary(Long ownerId, String yearMonth) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found!"));

        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate referenceMonthFirstDay = ym.atDay(1);

        List<MonthlyCharge> charges = monthlyChargeRepository.findByOwnerIdAndReferenceMonth(ownerId, referenceMonthFirstDay);

        List<MonthlyChargeDTO> chargeDTOs = monthlyChargeMapper.toDtoList(charges);

        BigDecimal total = charges.stream()
                .map(MonthlyCharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate invoiceDate = charges.isEmpty()
                ? YearMonth.parse(yearMonth).atDay(Math.min(invoiceDay, YearMonth.parse(yearMonth).lengthOfMonth()))
                : charges.get(0).getInvoiceDate();

        OwnerSummaryDTO summaryDTO = new OwnerSummaryDTO();
        summaryDTO.setOwnerId(owner.getId());
        summaryDTO.setOwnerName(owner.getName());
        summaryDTO.setOwnerEmail(owner.getEmail());
        summaryDTO.setReferenceMonth(yearMonth);
        summaryDTO.setInvoiceDate(invoiceDate);
        summaryDTO.setCharges(chargeDTOs);
        summaryDTO.setTotalAmount(total);


        addHateoasOwnerSummaryLinks(summaryDTO);
        return summaryDTO;
    }

    private MonthlyCharge createChargeForExpense(Expense expense, LocalDate referenceMonthFirstDay, String yearMonth,
                                                 LocalDate invoiceDate, LocalDate windowStart, LocalDate windowEnd) {
        YearMonth referenceMonth = YearMonth.from(referenceMonthFirstDay);
        YearMonth expenseStartMonth = YearMonth.from(expense.getStartDate());

        BigDecimal amount;
        Integer installmentNumber = null;

        if (expense.getRecurring() != null && expense.getRecurring()) {
            if (referenceMonth.isBefore(expenseStartMonth)) {
                logger.debug("Skipping recurring expense {}: reference {} before start {}", expense.getId(), referenceMonth, expenseStartMonth);
                return null;
            }
            amount = expense.getPrice();
        } else {
            int installments = expense.getInstallments() != null ? expense.getInstallments() : 1;
            if (installments == 1) {
                LocalDate expenseStartDate = expense.getStartDate();
                boolean inWindow = !expenseStartDate.isBefore(windowStart) && !expenseStartDate.isAfter(windowEnd);
                boolean sameMonth = YearMonth.from(expenseStartDate).equals(referenceMonth);
                if (!inWindow && !sameMonth) {
                    logger.debug("Skipping single-installment expense {}: startDate {} outside billing window {}..{} and not in reference month {}",
                            expense.getId(), expenseStartDate, windowStart, windowEnd, referenceMonth);
                    return null;
                }
                installmentNumber = 1;
                amount = expense.getInstallmentAmount();
                logger.debug("Including single-installment expense {}: startDate {} inWindow={} sameMonth={}", expense.getId(), expenseStartDate, inWindow, sameMonth);
            } else {
                YearMonth lastInstallmentMonth = expenseStartMonth.plusMonths(Math.max(0, installments - 1));
                if (referenceMonth.isBefore(expenseStartMonth) || referenceMonth.isAfter(lastInstallmentMonth)) {
                    logger.debug("Skipping non-recurring expense {}: reference {} not in [{}..{}]", expense.getId(),
                            referenceMonth, expenseStartMonth, lastInstallmentMonth);
                    return null;
                }
                long monthsPassed = ChronoUnit.MONTHS.between(expenseStartMonth, referenceMonth);
                installmentNumber = (int) monthsPassed + 1;
                amount = expense.getInstallmentAmount();
            }
        }

        MonthlyCharge charge = new MonthlyCharge(
                expense,
                expense.getOwner(),
                amount,
                referenceMonthFirstDay,
                invoiceDate,
                installmentNumber
        );

        return monthlyChargeRepository.save(charge);
    }

    private MonthlyCharge upsertChargeForExpense(Expense expense, LocalDate referenceMonthFirstDay,
                                                 String yearMonth, LocalDate invoiceDate,
                                                 LocalDate windowStart, LocalDate windowEnd) {
        Optional<MonthlyCharge> existing = monthlyChargeRepository.findByExpenseIdAndReferenceMonth(expense.getId(), referenceMonthFirstDay);
        if (existing.isPresent()) {
            MonthlyCharge charge = existing.get();
            YearMonth referenceMonth = YearMonth.from(referenceMonthFirstDay);
            YearMonth expenseStartMonth = YearMonth.from(expense.getStartDate());

            if (expense.getRecurring() != null && expense.getRecurring()) {
                if (referenceMonth.isBefore(expenseStartMonth)) return null;
                charge.setAmount(expense.getPrice());
                charge.setInstallmentNumber(null);
            } else {
                int installments = expense.getInstallments() != null ? expense.getInstallments() : 1;
                if (installments == 1) {
                    LocalDate expenseStartDate = expense.getStartDate();
                    boolean inWindow = !expenseStartDate.isBefore(windowStart) && !expenseStartDate.isAfter(windowEnd);
                    boolean sameMonth = YearMonth.from(expenseStartDate).equals(referenceMonth);
                    if (!inWindow && !sameMonth) {
                        return null;
                    }
                    charge.setInstallmentNumber(1);
                    charge.setAmount(expense.getInstallmentAmount());
                    logger.debug("Including single-installment (upsert) expense {}: startDate {} inWindow={} sameMonth={}",
                            expense.getId(), expenseStartDate, inWindow, sameMonth);
                } else {
                    YearMonth lastInstallmentMonth = expenseStartMonth.plusMonths(Math.max(0, installments - 1));
                    if (referenceMonth.isBefore(expenseStartMonth) || referenceMonth.isAfter(lastInstallmentMonth)) {
                        return null;
                    }
                    long monthsPassed = ChronoUnit.MONTHS.between(expenseStartMonth, referenceMonth);
                    int currentInstallment = (int) monthsPassed + 1;
                    charge.setInstallmentNumber(currentInstallment);
                    charge.setAmount(expense.getInstallmentAmount());
                }
            }

            charge.setInvoiceDate(invoiceDate);
            charge.setOwner(expense.getOwner());

            logger.info("Updating MonthlyCharge id={} for expense {} reference={}", charge.getId(), expense.getId(), referenceMonthFirstDay);
            return monthlyChargeRepository.save(charge);
        } else {
            MonthlyCharge created = createChargeForExpense(expense, referenceMonthFirstDay, yearMonth, invoiceDate, windowStart, windowEnd);
            if (created != null) {
                logger.info("Created MonthlyCharge id={} for expense {} reference={}", created.getId(), expense.getId(), referenceMonthFirstDay);
            } else {
                logger.debug("No MonthlyCharge created for expense {} reference={}", expense.getId(), referenceMonthFirstDay);
            }
            return created;
        }
    }

    public void addHateoasOwnerSummaryLinks(OwnerSummaryDTO dto) {
        dto.add(linkTo(methodOn(MonthlyChargeController.class).getOwnerSummary(dto.getReferenceMonth(), dto.getOwnerId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(MonthlyChargeController.class).generateCharges(dto.getReferenceMonth())).withRel("monthly-charges").withType("POST"));
    }
}
