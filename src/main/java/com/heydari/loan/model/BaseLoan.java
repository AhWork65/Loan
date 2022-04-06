package com.heydari.loan.model;

import lombok.*;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BaseLoan {
    protected Integer totalInstallments;
    protected Integer remainingInstallments;
    protected BigDecimal installmentsPrice;
    protected BigDecimal loanPrice;
    protected LoanInterestRate loanInterestRate;
}
