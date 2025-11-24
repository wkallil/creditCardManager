package wkallil.card.creditCardManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wkallil.card.creditCardManager.models.MonthlyCharge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MonthlyChargeRepository extends JpaRepository<MonthlyCharge, Long> {

    boolean existsByExpenseIdAndReferenceMonth(Long expenseId, LocalDate referenceMonth);

    Optional<MonthlyCharge> findByExpenseIdAndReferenceMonth(Long expenseId, LocalDate referenceMonth);

    List<MonthlyCharge> findByOwnerIdAndReferenceMonth(Long ownerId, LocalDate referenceMonth);

    List<MonthlyCharge> findByReferenceMonth(LocalDate referenceMonth);

    @Query("""
            SELECT SUM(mc.amount)
            FROM MonthlyCharge mc
            WHERE mc.owner.id = :ownerId
            AND mc.referenceMonth = :referenceMonth
            """
    )
    BigDecimal sumByOwnerIdAndReferenceMonth(Long ownerId, LocalDate referenceMonth);

}
