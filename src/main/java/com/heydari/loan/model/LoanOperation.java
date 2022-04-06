package com.heydari.loan.model;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode

public class LoanOperation extends Loan {
    private String depositNumber;
}