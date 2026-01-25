package org.learncode.aama.entites;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class LoanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long loanReqId;
    private Double Amount;
    private String purpose;
    private LocalDate createdAt=LocalDate.now();
    private String status="pending";

    @OneToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    private Users users;

}
