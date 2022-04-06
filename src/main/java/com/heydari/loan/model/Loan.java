package com.heydari.loan.model;

import com.heydari.loan.model.deposit.Deposit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Loan extends BaseLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private LoanType type;
    @Temporal(TemporalType.DATE)
    private Date openingDate;
    private LoanStatus status;
    @ManyToOne
    private Deposit deposit;

}
