package wkallil.card.creditCardManager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Expense name is mandatory")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Expense price is mandatory")
    @DecimalMin(value = "0.01", message = "Expense price must be greater than zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Number of installments is mandatory")
    @Min(value = 1, message = "There must be at least one installment")
    @Column(nullable = false)
    private Integer installments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;

    @NotNull(message = "Start date is mandatory")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<MonthlyCharge> monthlyCharges = new ArrayList<>();

    public Expense() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRecurring == null) {
            isRecurring = false;
        }
    }

    public BigDecimal getInstallmentAmount() {
        return price.divide(BigDecimal.valueOf(installments), 2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Boolean getRecurring() {
        return isRecurring;
    }

    public void setRecurring(Boolean recurring) {
        isRecurring = recurring;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<MonthlyCharge> getMonthlyCharges() {
        return monthlyCharges;
    }

    public void setMonthlyCharges(List<MonthlyCharge> monthlyCharges) {
        this.monthlyCharges = monthlyCharges;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id) && Objects.equals(name, expense.name) && Objects.equals(price, expense.price) && Objects.equals(installments, expense.installments) && Objects.equals(owner, expense.owner) && Objects.equals(isRecurring, expense.isRecurring) && Objects.equals(startDate, expense.startDate) && Objects.equals(createdAt, expense.createdAt) && Objects.equals(monthlyCharges, expense.monthlyCharges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, installments, owner, isRecurring, startDate, createdAt, monthlyCharges);
    }
}
