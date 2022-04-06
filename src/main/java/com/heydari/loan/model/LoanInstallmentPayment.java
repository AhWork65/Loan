package com.heydari.loan.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class LoanInstallmentPayment {
    private String depositNumber;
    private String loanNumber;
}
