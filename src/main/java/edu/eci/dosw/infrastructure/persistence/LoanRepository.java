package edu.eci.dosw.persistence.repository;

import edu.eci.dosw.core.model.LoanStatus;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByUserAndStatus(LibraryUser user, LoanStatus status);

    List<Loan> findByUserOrderByLoanDateDesc(LibraryUser user);
}
