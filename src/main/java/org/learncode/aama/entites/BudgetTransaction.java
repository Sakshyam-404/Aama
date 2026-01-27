package org.learncode.aama.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class BudgetTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Budget_Id;
    private Double totalBudget;
    private Double availableBudget;
    @OneToOne(fetch = FetchType.EAGER)
    private Users users;


}
