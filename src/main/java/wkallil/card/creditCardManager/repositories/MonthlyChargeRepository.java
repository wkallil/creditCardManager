package wkallil.card.creditCardManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wkallil.card.creditCardManager.models.MonthlyCharge;

import java.math.BigDecimal;
import java.util.List;

public interface MonthlyChargeRepository extends JpaRepository<MonthlyCharge, Long> {

    boolean existsByExpenseIdAndReferenceMonth(Long expenseId, String referenceMonth);

    List<MonthlyCharge> findByOwnerIdAndReferenceMonth(Long ownerId, String referenceMonth);

    List<MonthlyCharge> findByReferenceMonth(String referenceMonth);

    @Query("""
            SELECT SUM(mc.amount)
            FROM MonthlyCharge mc
            WHERE mc.owner..id = :ownerId
            AND mc.referenceMonth = :referenceMonth
            """
    )
    BigDecimal sumByOwnerIdAndReferenceMonth(Long ownerId, String referenceMonth);

}
