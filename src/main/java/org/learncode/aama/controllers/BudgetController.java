package org.learncode.aama.controllers;


import org.learncode.aama.Dao.UserRepo;
import org.learncode.aama.Dao.budgetRepo;

import org.learncode.aama.Dto.BudgetDto;
import org.learncode.aama.entites.BudgetTransaction;
import org.learncode.aama.entites.UserPrincipal;
import org.learncode.aama.entites.Users;
import org.learncode.aama.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins ="http://localhost:5173")
public class BudgetController {

    @Autowired
    private DepositService depositService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private budgetRepo budgetRepo1;


    @GetMapping("/admin/get-budget-info")
    @PreAuthorize("hasRole('ADMIN')")
    public BudgetDto getBudget(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users user = principal.getUser();
        BudgetTransaction budget = budgetRepo1.findBudgetTransactionByUsersUserID(user.getUserID());
        if (budget == null) {
            // return default zero budget if none exists yet
            return new BudgetDto(0.0, 0.0);
        }
        return new BudgetDto(
                budget.getTotalBudget(),
                budget.getAvailableBudget()
        );



    }
    @PostMapping("/admin/create-deposits")
    @PreAuthorize("hasRole('ADMIN')")
    public BudgetDto createDeposits(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users user = principal.getUser();
        Double amount = 1000D;
        depositService.addLumpSumDeposit(user.getUserID(),amount, "Jan");
        BudgetDto budget = depositService.manageBudget(user.getUserID(), amount);
        if (budget == null) {
            // return default zero budget if none exists yet
            return new BudgetDto(0.0, 0.0);
        }
        return budget;

    }
}
