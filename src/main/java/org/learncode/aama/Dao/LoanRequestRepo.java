package org.learncode.aama.Dao;

import org.learncode.aama.entites.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRequestRepo extends JpaRepository<LoanRequest,Long> {
    // Changed to return List since we now have OneToMany relationship
    List<LoanRequest> findByUsers_UserID(Long usersUserID);

    // Optional: Find by user and status
    List<LoanRequest> findByUsers_UserIDAndStatus(Long usersUserID, String status);

    // Optional: Find pending requests for a user
    Optional<LoanRequest> findFirstByUsers_UserIDAndStatusOrderByCreatedAtDesc(Long usersUserID, String status);

    LoanRequestRepo findLoanRequestsByStatus(String status);
}