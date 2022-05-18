package antifraud.repository;

import antifraud.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByNumberAndDateAfterAndDateBefore(String number, LocalDateTime from, LocalDateTime to);

    List<Transaction> findAllByOrderByIdAsc();

    List<Transaction> findAllByNumberOrderByIdAsc(String number);

    Boolean existsByNumber(String number);
}
