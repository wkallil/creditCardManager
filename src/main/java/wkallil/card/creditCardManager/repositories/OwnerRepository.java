package wkallil.card.creditCardManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wkallil.card.creditCardManager.models.Owner;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsByEmail(String email);
    Optional<Owner> findByEmail(String email);
}
