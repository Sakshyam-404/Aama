package org.learncode.aama.entites;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"loan", "deposit", "loanRequests", "password"})  // Changed loanRequest to loanRequests
    private Users users;

    private Double principal;
    private Double interestAmount;
    private Double totalPayable;
    private Double interestRate=5.0;
    private Integer durationMonths=1;
    private LocalDateTime startDate = LocalDateTime.now();
    private Double remainingBalance;
    private String status = "ACTIVE";
}