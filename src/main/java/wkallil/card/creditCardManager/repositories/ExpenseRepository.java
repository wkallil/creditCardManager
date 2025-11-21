package wkallil.card.creditCardManager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wkallil.card.creditCardManager.models.Expense;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByOwnerId(Long ownerId);

    @Query("""
            SELECT e FROM Expense e
            WHERE e.startDate <= :endDate
              AND (e.Recurring = true
                    OR e.startDate >= :startDate)
            """
    )
    List<Expense> findActiveExpensesForMonth(LocalDate startDate, LocalDate endDate);
}
