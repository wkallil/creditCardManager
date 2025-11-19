package wkallil.card.creditCardManager.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class CreateOwnerDTO {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String Email;

    public CreateOwnerDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateOwnerDTO that = (CreateOwnerDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(Email, that.Email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, Email);
    }
}
