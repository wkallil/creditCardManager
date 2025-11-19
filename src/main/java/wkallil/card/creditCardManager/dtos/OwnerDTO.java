package wkallil.card.creditCardManager.dtos;

import java.time.LocalDateTime;
import java.util.Objects;

public class OwnerDTO {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public OwnerDTO() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        OwnerDTO ownerDTO = (OwnerDTO) o;
        return Objects.equals(id, ownerDTO.id) && Objects.equals(name, ownerDTO.name) && Objects.equals(email, ownerDTO.email) && Objects.equals(createdAt, ownerDTO.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, createdAt);
    }
}
