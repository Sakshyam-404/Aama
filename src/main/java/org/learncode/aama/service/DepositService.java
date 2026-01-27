package org.learncode.aama.service;

import jakarta.transaction.Transactional;
import org.learncode.aama.Dao.DepositRepo;
import org.learncode.aama.Dao.UserRepo;
import org.learncode.aama.Dao.budgetRepo;

import org.learncode.aama.Dto.BudgetDto;
import org.learncode.aama.entites.BudgetTransaction;
import org.learncode.aama.entites.Deposit;
import org.learncode.aama.entites.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DepositService {
    @Autowired
    private DepositRepo depositRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private budgetRepo budgetRepo;

    public void manageLoanrequest(Long userID, Double amount) {
        BudgetTransaction budget = budgetRepo.findBudgetTransactionByUsersUserID(userID);
        budget.setAvailableBudget(budget.getAvailableBudget()-amount);
        budgetRepo.save(budget);

    }
    public void manageLoanrequestforpaid(Long userID, Double amount) {
        BudgetTransaction budget = budgetRepo.findBudgetTransactionByUsersUserID(userID);
        budget.setAvailableBudget(budget.getAvailableBudget()+amount);
        budgetRepo.save(budget);

    }

    @Transactional
    public void addDepositForUser(Long adminId, Long userId, Double amount, String month) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Double nofuser = userRepo.countUsersByRole("USER");

        Deposit deposit = new Deposit();
        deposit.setUsers(user);
        deposit.setAmount(amount);
        deposit.setMonth(month);
        depositRepo.save(deposit);



    }


    @Transactional
    public void addLumpSumDeposit(Long adminID, Double amount, String month) {
        List<Users> allUsers = userRepo.findByRole("USER");


        for (Users u : allUsers) {
            addDepositForUser(adminID, u.getUserID(), amount, month);
        }
    }

    @Transactional
    public BudgetDto manageBudget(Long adminId, Double amount) {
        Double nofuser = userRepo.countUsersByRole("USER");
        BudgetTransaction budgetTransaction = budgetRepo.findBudgetTransactionByUsersUserID(adminId);
        if (budgetTransaction == null) {
            BudgetTransaction tx = new BudgetTransaction();

            tx.setTotalBudget(amount*nofuser);
            tx.setAvailableBudget(amount*nofuser);
            Users usersByUserID = userRepo.getUsersByUserID(adminId);
            tx.setUsers(usersByUserID);
            budgetRepo.save(tx);
            return new BudgetDto(
                    tx.getTotalBudget(),
                    tx.getAvailableBudget()
            );
        }
        else{
            budgetTransaction.setTotalBudget(budgetTransaction.getTotalBudget()+(amount*nofuser));
            budgetTransaction.setAvailableBudget(budgetTransaction.getAvailableBudget()+(amount*nofuser));
            budgetRepo.save(budgetTransaction);
            return new BudgetDto(
                    budgetTransaction.getTotalBudget(),
                    budgetTransaction.getAvailableBudget()
            );

        }



    }
}

