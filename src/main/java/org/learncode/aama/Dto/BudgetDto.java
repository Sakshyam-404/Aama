package org.learncode.aama.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BudgetDto {
    private Double totalBudget;
    private Double availableBudget;
}
