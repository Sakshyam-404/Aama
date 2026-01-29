package org.learncode.aama.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberDashboardDto {
    private Long userId;
    private String name;
    private String phoneNumber;
    private int loansTaken;
    private double totalBorrowed;
    private String loanPurpose;
    private Double principal;
    private String status;
    private Double interestRate;
    private Integer durationMonths;
    private LocalDateTime startdate;
    private String role;
    private Double totalDeposit;
    private Double totalpayableamt;

}
