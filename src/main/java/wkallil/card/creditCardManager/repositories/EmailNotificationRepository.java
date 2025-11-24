package wkallil.card.creditCardManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wkallil.card.creditCardManager.models.EmailNotification;

import java.time.LocalDate;
import java.util.Optional;

public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Long> {

    boolean existsByOwnerIdAndReferenceMonth(Long id, LocalDate referenceMonth);

    Optional<EmailNotification> findByOwnerIdAndReferenceMonth(Long ownerId, LocalDate referenceMonth);
}
