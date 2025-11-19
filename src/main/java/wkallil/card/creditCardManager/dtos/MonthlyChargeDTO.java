package wkallil.card.creditCardManager.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class MonthlyChargeDTO {

    private Long id;
    private String expenseName;
    private BigDecimal amount;
    private Integer installmentNumber;
    private Integer totalInstallments;
    private Boolean isRecurring;
    private LocalDate invoiceDate;

    public MonthlyChargeDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public Integer getTotalInstallments() {
        return totalInstallments;
    }

    public void setTotalInstallments(Integer totalInstallments) {
        this.totalInstallments = totalInstallments;
    }

    public Boolean getRecurring() {
        return isRecurring;
    }

    public void setRecurring(Boolean recurring) {
        isRecurring = recurring;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MonthlyChargeDTO that = (MonthlyChargeDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(expenseName, that.expenseName) && Objects.equals(amount, that.amount) && Objects.equals(installmentNumber, that.installmentNumber) && Objects.equals(totalInstallments, that.totalInstallments) && Objects.equals(isRecurring, that.isRecurring) && Objects.equals(invoiceDate, that.invoiceDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expenseName, amount, installmentNumber, totalInstallments, isRecurring, invoiceDate);
    }
}
