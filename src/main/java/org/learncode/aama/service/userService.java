package org.learncode.aama.service;

import org.learncode.aama.Dao.LoanRepo;
import org.learncode.aama.Dao.UserRepo;
import org.learncode.aama.Dto.MemberDashboardDto;
import org.learncode.aama.entites.Loan;
import org.learncode.aama.entites.LoanRequest;
import org.learncode.aama.entites.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class userService {
    @Autowired
    private UserRepo userDao;
    @Autowired
    private LoanRepo loanRepo;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public Users saveUser(Users users){
        users.setPassword(bCryptPasswordEncoder.encode(users.getPassword()));
        Users save = userDao.save(users);
        return save;
    }

    public MemberDashboardDto getMemberDashboardStats(Long userId) {
        Users user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Loan> allLoans = user.getLoan();

        // Filter only ACTIVE loans for statistics
        List<Loan> activeLoans = allLoans.stream()
                .filter(loan -> "ACTIVE".equals(loan.getStatus()))
                .collect(Collectors.toList());

        // Get the most recent loan request (prefer pending, then most recent)
        List<LoanRequest> loanRequests = user.getLoanRequests();
        LoanRequest loanRequest = null;
        if (loanRequests != null && !loanRequests.isEmpty()) {
            // First try to find a pending one
            loanRequest = loanRequests.stream()
                    .filter(lr -> "pending".equalsIgnoreCase(lr.getStatus()))
                    .findFirst()
                    .orElse(null);

            // If no pending, get the most recent one
            if (loanRequest == null) {
                loanRequest = loanRequests.stream()
                        .max((lr1, lr2) -> lr1.getCreatedAt().compareTo(lr2.getCreatedAt()))
                        .orElse(null);
            }
        }

        // Handle empty loans safely
        Loan firstLoan = activeLoans.isEmpty() ? null : activeLoans.get(0);

        // Statistics - only count ACTIVE loans
        int loansTaken = activeLoans.size();
        double totalBorrowed = activeLoans.stream()
                .mapToDouble(loan -> loan.getPrincipal() != null ? loan.getPrincipal() : 0.0)
                .sum();

        // Safe getters for nullable fields
        String loanPurpose = loanRequest != null ? loanRequest.getPurpose() : null;
        Double principal = firstLoan != null ? firstLoan.getPrincipal() : null;
        String status = firstLoan != null ? firstLoan.getStatus() : null;
        Double interestRate = firstLoan != null ? firstLoan.getInterestRate() : null;
        Integer durationMonths = firstLoan != null ? firstLoan.getDurationMonths() : null;
        LocalDate startdate = firstLoan != null ? firstLoan.getStartDate() : null;

        return new MemberDashboardDto(
                user.getUserID(),
                user.getName(),
                user.getPhonenumber(),
                loansTaken,
                totalBorrowed,
                loanPurpose,
                principal,
                status,
                interestRate,
                durationMonths,
                startdate,
                user.getRole()
        );
    }

}