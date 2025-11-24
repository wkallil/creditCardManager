package wkallil.card.creditCardManager.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@JsonPropertyOrder({"name", "price", "installments", "ownerId", "recurring", "startDate"})
public class CreateExpenseDTO extends RepresentationModel<CreateExpenseDTO> {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Installments is mandatory")
    @Min(value = 1, message = "Installments must be at least 1")
    private Integer installments;

    @NotNull(message = "Owner ID is mandatory")
    private Long ownerId;

    private Boolean isRecurring;

    @NotNull(message = "Start date is mandatory")
    private LocalDate startDate;

    public CreateExpenseDTO() {
        this.isRecurring = false;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateExpenseDTO that = (CreateExpenseDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(installments, that.installments) && Objects.equals(ownerId, that.ownerId) && Objects.equals(isRecurring, that.isRecurring) && Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, installments, ownerId, isRecurring, startDate);
    }
}
