package org.learncode.aama.controllers;

import org.learncode.aama.Dao.LoanRequestRepo;
import org.learncode.aama.entites.*;
import org.learncode.aama.service.LoanService;
import org.learncode.aama.service.noticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins ="http://localhost:5173")
@RestController
@RequestMapping()
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private noticeService noticeService;

    @Autowired
    private LoanRequestRepo loanRequestRepo;


    @PostMapping("/loan-request")
    public Notice createLoanRequest(@RequestBody LoanRequest loanRequest){
        // Get authenticated user from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users user = principal.getUser();

        // Create loan and notice
        Notice loan = loanService.createLoan(user.getUserID(), loanRequest);
        Notice notice = noticeService.createNotice(loan, user.getUserID());
        return notice;
    }


    @PostMapping("admin/approve/{loanId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Notice approveLoan(@PathVariable("loanId") Long loanId){
        // Get admin user from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users admin = principal.getUser();

        // Optional: check if admin has role
        if(!admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can approve loans");
        }

        return loanService.approve(loanId, admin.getUserID());
    }


    @PostMapping("admin/reject/{loanId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectLoan(@PathVariable("loanId") Long loanId){
        // Get admin user from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users admin = principal.getUser();

        if(!admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can reject loans");
        }

        loanService.reject(loanId, admin.getUserID());
        return "Loan rejected";
    }


    @GetMapping("admin/loan-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanRequest> getAllLoanRequests() {
        // Get admin user from JWT (optional - already checked by @PreAuthorize)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users admin = principal.getUser();

        if (!admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can view all loan requests");
        }

        return loanService.getAllLoanRequests();
    }
    // Add to LoanController.java

    @GetMapping("admin/loans/active")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Loan> getAllActiveLoans() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users admin = principal.getUser();

        if (!admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can view all active loans");
        }

        return loanService.getAllActiveLoans();
    }

    @GetMapping("admin/loan-history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoanRequest> getLoanHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users admin = principal.getUser();

        if (!admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can view loan history");
        }

        return loanService.getLoanHistory();
    }


    @PostMapping("admin/loans/{loanId}/{userid}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public Loan markLoanAsPaid(@PathVariable("loanId") Long loanId, @PathVariable("userid") Long userID) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users admin = principal.getUser();

        if (!admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can mark loans as paid");
        }

        return loanService.markLoanAsPaid(loanId, admin.getUserID(), userID);
    }

    @GetMapping("admin/loans")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Loan> getAllLoans() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users admin = principal.getUser();

        if (!admin.getRole().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only admins can view all loans");
        }

        return loanService.getAllLoans();
    }
    @GetMapping("/my-loan-history")
    public List<LoanRequest> getMyLoanHistory() {
        // Get authenticated user from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users user = principal.getUser();

        return loanService.getMemberLoanHistory(user.getUserID());
    }


}

