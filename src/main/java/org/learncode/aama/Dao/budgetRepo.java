package org.learncode.aama.Dao;

import org.learncode.aama.entites.BudgetTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface budgetRepo extends JpaRepository<BudgetTransaction, Long> {
    BudgetTransaction findBudgetTransactionByUsersUserID(Long usersUserID);
}
