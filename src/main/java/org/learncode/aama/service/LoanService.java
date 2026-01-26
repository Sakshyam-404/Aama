package org.learncode.aama.service;

import org.aspectj.weaver.ast.Not;
import org.learncode.aama.Dao.*;
import org.learncode.aama.entites.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    @Autowired
    private LoanRepo loanRepo;
    @Autowired
    private LoanRequestRepo loanRequestRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private NoticeRepo noticeRepo;

    public Notice createLoan(Long userId, LoanRequest loanRequest){
        Users users = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the user already has a loan request
        if (loanRequestRepo.findByUsers_UserID(userId) != null) {
            throw new IllegalStateException("User already has a loan request");
        }

        // Set the user
        loanRequest.setUsers(users);

        // Save the loan request
        LoanRequest savedLoanRequest = loanRequestRepo.save(loanRequest);

        // Create a notice
        Notice notice = new Notice();
        notice.setType("Loan Request for Rs " + savedLoanRequest.getAmount());
        notice.setPurpose(savedLoanRequest.getPurpose() + "  Status : " + savedLoanRequest.getStatus());
        notice.setLoanid(loanRequest.getLoanReqId());
        notice.setNoticeCreator(users.getName());

        return notice;
    }

    public Notice approve(Long loanId,Long adminId){
        Users admin = userRepo.getById(adminId);
        if(admin.getRole().equals("ADMIN")){
            Optional<LoanRequest> loanreq = loanRequestRepo.findById(loanId);
            LoanRequest loanRequest = loanreq.get();
            Notice noticeByPurpose = noticeRepo.getNoticeByPurpose(loanRequest.getPurpose() + "  Status : " + loanRequest.getStatus());
            loanRequest.setStatus("Approved");
            loanRequestRepo.save(loanRequest);
            Loan loan= new Loan();
            loan.setPrincipal(loanRequest.getAmount());
            loan.setUsers(loanRequest.getUsers());
            loanRepo.save(loan);
            noticeByPurpose.setPurpose(loanRequest.getPurpose() + "  Status : " + loanRequest.getStatus());
            noticeByPurpose.setLoanid(loan.getId());
            Notice save = noticeRepo.save(noticeByPurpose);
            return save;

        }
        else{
            System.out.println("Only admin can approve request");
            return null;
        }





    }

    public Notice reject(Long loanId,Long adminId){
        Users admin = userRepo.getById(adminId);
        if(admin.getRole().equals("ADMIN")){
            Optional<LoanRequest> loanreq = loanRequestRepo.findById(loanId);
            LoanRequest loanRequest = loanreq.get();
            Notice noticeByPurpose = noticeRepo.getNoticeByPurpose(loanRequest.getPurpose() + "  Status : " + loanRequest.getStatus());
            loanRequest.setStatus("Rejected");
            loanRequestRepo.save(loanRequest);
            noticeByPurpose.setPurpose(loanRequest.getPurpose() + "  Status : " + loanRequest.getStatus());
            Notice save = noticeRepo.save(noticeByPurpose);
            return save;
        }
        else{
            System.out.println("Only admin can approve request");
            return null;
        }



    }


    public List<LoanRequest> getAllLoanRequests() {
        List<LoanRequest> all = loanRequestRepo.findAll();
        return all;
    }
    // Add to LoanService.java

    public List<Loan> getAllActiveLoans() {
        return loanRepo.findAll().stream()
                .filter(loan -> "ACTIVE".equals(loan.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<LoanRequest> getLoanHistory() {
        // Get all loan requests that are not pending (approved or rejected)
        return loanRequestRepo.findAll().stream()
                .filter(lr -> !"pending".equalsIgnoreCase(lr.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    public Loan markLoanAsPaid(Long loanId, Long adminId) {
        Users admin = userRepo.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new RuntimeException("Only admins can mark loans as paid");
        }

        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        // Check if loan is already paid
        if ("PAID".equalsIgnoreCase(loan.getStatus())) {
            throw new IllegalStateException("Loan is already marked as paid");
        }

        // Update loan status
        loan.setStatus("PAID");
        loan.setRemainingBalance(0.0);

        // Save and return
        return loanRepo.save(loan);
    }

    // Add this method for loan history (includes PAID loans)
    public List<Loan> getAllLoans() {
        return loanRepo.findAll();
    }
}
