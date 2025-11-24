package wkallil.card.creditCardManager.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({"ownerId", "ownerName", "ownerEmail", "referenceMonth", "invoiceDate", "charges", "totalAmount"})
public class OwnerSummaryDTO extends RepresentationModel<OwnerSummaryDTO> {

    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private String referenceMonth;
    private LocalDate invoiceDate;
    private List<MonthlyChargeDTO> charges;
    private BigDecimal totalAmount;

    public OwnerSummaryDTO() {
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
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

    public List<MonthlyChargeDTO> getCharges() {
        return charges;
    }

    public void setCharges(List<MonthlyChargeDTO> charges) {
        this.charges = charges;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OwnerSummaryDTO that = (OwnerSummaryDTO) o;
        return Objects.equals(ownerId, that.ownerId) && Objects.equals(ownerName, that.ownerName) && Objects.equals(ownerEmail, that.ownerEmail) && Objects.equals(referenceMonth, that.referenceMonth) && Objects.equals(invoiceDate, that.invoiceDate) && Objects.equals(charges, that.charges) && Objects.equals(totalAmount, that.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, ownerName, ownerEmail, referenceMonth, invoiceDate, charges, totalAmount);
    }
}
