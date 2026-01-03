package org.learncode.aama.Dao;

import org.learncode.aama.entites.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LoanRequestRepo extends JpaRepository<LoanRequest,Long> {
    LoanRequest findByUsers_UserID(Long usersUserID);

    LoanRequestRepo findLoanRequestsByStatus(String status);


}
