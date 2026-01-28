package org.learncode.aama.Dao;

import org.learncode.aama.entites.Loan;
import org.learncode.aama.entites.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepo extends JpaRepository<Loan, Long> {
    Loan findLoansByUsers_UserID(Long usersUserID);

    Loan findLoansByStatus(String status);
    List<Loan> findByUsers_UserID(Long userID);

    int countLoanByUsers(Users users);

    int countLoanByUsers_UserID(Long usersUserID);

    int getLoansById(Long id);
}
