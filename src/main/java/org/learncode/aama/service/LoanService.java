package org.learncode.aama.service;

import org.aspectj.weaver.ast.Not;
import org.learncode.aama.Dao.*;
import org.learncode.aama.entites.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Transactional
    public Notice createLoan(Long userId, LoanRequest loanRequest){
        Users users = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user has any PENDING loan request using the OneToMany relationship
        boolean hasPendingRequest = users.getLoanRequests().stream()
                .anyMatch(lr -> "pending".equalsIgnoreCase(lr.getStatus()));

        if (hasPendingRequest) {
            throw new IllegalStateException("User already has a pending loan request. Please wait for approval or rejection.");
        }

        // Create a new loan request (we keep all history now)
        loanRequest.setUsers(users);
        loanRequest.setStatus("pending");
        LoanRequest savedLoanRequest = loanRequestRepo.save(loanRequest);

        // Add to user's list (optional, but keeps relationship in sync)
        users.getLoanRequests().add(savedLoanRequest);
        userRepo.save(users);

        // Create a notice
        Notice notice = new Notice();
        notice.setType("Loan Request for Rs " + savedLoanRequest.getAmount());
        notice.setPurpose(savedLoanRequest.getPurpose() + "  Status : " + savedLoanRequest.getStatus());
        notice.setLoanid(savedLoanRequest.getLoanReqId());
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

    public List<Loan> getAllActiveLoans() {
        return loanRepo.findAll().stream()
                .filter(loan -> "ACTIVE".equals(loan.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<LoanRequest> getLoanHistory() {
        // Get all loan requests that are not pending (approved, rejected, or paid)
        return loanRequestRepo.findAll().stream()
                .filter(lr -> !"pending".equalsIgnoreCase(lr.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    // ADD THIS: Get loan history for a specific member
    public List<LoanRequest> getMemberLoanHistory(Long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Return all loan requests for this user (excluding pending ones)
        return user.getLoanRequests().stream()
                .filter(lr -> !"pending".equalsIgnoreCase(lr.getStatus()))
                .sorted((lr1, lr2) -> lr2.getCreatedAt().compareTo(lr1.getCreatedAt())) // Most recent first
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public Loan markLoanAsPaid(Long loanId, Long adminId, Long userID) {
        Users admin = userRepo.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new RuntimeException("Only admins can mark loans as paid");
        }

        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        // Verify the loan belongs to the user
        if (!loan.getUsers().getUserID().equals(userID)) {
            throw new IllegalArgumentException("Loan does not belong to the specified user");
        }

        // Check if loan is already paid
        if ("PAID".equalsIgnoreCase(loan.getStatus())) {
            throw new IllegalStateException("Loan is already marked as paid");
        }

        // Find the LoanRequest that matches this loan (by user and amount)
        Users loanUser = loan.getUsers();
        LoanRequest loanRequest = loanUser.getLoanRequests().stream()
                .filter(lr -> "Approved".equalsIgnoreCase(lr.getStatus())
                        && lr.getAmount().equals(loan.getPrincipal())
                        && !"PAID".equalsIgnoreCase(lr.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No approved loan request found for this loan"));

        // Update loan status
        loan.setStatus("PAID");
        loan.setRemainingBalance(0.0);

        // Update loan request status
        loanRequest.setStatus("PAID");

        // Update notice
        String oldPurpose = loanRequest.getPurpose() + "  Status : " + loanRequest.getStatus();
        Notice noticeByPurpose = noticeRepo.getNoticeByPurpose(oldPurpose);
        if (noticeByPurpose != null) {
            noticeByPurpose.setPurpose(loanRequest.getPurpose() + "  Status : " + loanRequest.getStatus());
            noticeRepo.save(noticeByPurpose);
        }

        // Save both
        loanRequestRepo.save(loanRequest);
        return loanRepo.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepo.findAll();
    }
}