package com.heydari.loan.repository;



import com.heydari.loan.model.Loan;
import com.heydari.loan.model.deposit.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByDeposit(Deposit deposit);
    Loan findByNumber(String number);
}
