package wkallil.card.creditCardManager.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "monthly_charges",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"expense_id", "reference_month"}
        ))
public class MonthlyCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "reference_month", nullable = false, length = 7)
    private String referenceMonth;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public MonthlyCharge() {
    }

    public MonthlyCharge(Expense expense, Owner owner, BigDecimal amount, String referenceMonth, LocalDate invoiceDate, Integer installmentNumber) {
        this.expense = expense;
        this.owner = owner;
        this.amount = amount;
        this.referenceMonth = referenceMonth;
        this.invoiceDate = invoiceDate;
        this.installmentNumber = installmentNumber;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReferenceMonth() {
        return referenceMonth;
    }

    public void setReferenceMonth(String referenceMonth) {
        this.referenceMonth = referenceMonth;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MonthlyCharge that = (MonthlyCharge) o;
        return Objects.equals(id, that.id) && Objects.equals(expense, that.expense) && Objects.equals(owner, that.owner) && Objects.equals(amount, that.amount) && Objects.equals(referenceMonth, that.referenceMonth) && Objects.equals(invoiceDate, that.invoiceDate) && Objects.equals(installmentNumber, that.installmentNumber) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expense, owner, amount, referenceMonth, invoiceDate, installmentNumber, createdAt);
    }
}
