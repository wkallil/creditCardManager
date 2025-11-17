package wkallil.card.creditCardManager.models;

import jakarta.persistence.*;
import wkallil.card.creditCardManager.models.enums.EmailStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "email_notifications")
public class EmailNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "reference_month", nullable = false)
    private String referenceMonth;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    public EmailNotification() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EmailNotification that = (EmailNotification) o;
        return Objects.equals(id, that.id) && Objects.equals(owner, that.owner) && Objects.equals(referenceMonth, that.referenceMonth) && Objects.equals(invoiceDate, that.invoiceDate) && Objects.equals(totalAmount, that.totalAmount) && Objects.equals(sentAt, that.sentAt) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, referenceMonth, invoiceDate, totalAmount, sentAt, status);
    }
}
